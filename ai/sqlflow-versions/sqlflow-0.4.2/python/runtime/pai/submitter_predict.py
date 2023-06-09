# Copyright 2020 The SQLFlow Authors. All rights reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
import tempfile

from runtime import db
from runtime.diagnostics import SQLFlowDiagnostic
from runtime.model import EstimatorType
from runtime.pai import cluster_conf, pai_model, table_ops
from runtime.pai.create_result_table import create_predict_result_table
from runtime.pai.get_pai_tf_cmd import (ENTRY_FILE, JOB_ARCHIVE_FILE,
                                        PARAMS_FILE, get_pai_tf_cmd)
from runtime.pai.prepare_archive import prepare_archive
from runtime.pai.submit_pai_task import submit_pai_task


def get_pai_predict_cmd(datasource, project, oss_model_path, model_name,
                        predict_table, result_table, model_type, model_params,
                        job_file, params_file, cwd):
    """Get predict command for PAI task

    Args:
        datasource: current datasource
        project: current project
        oss_model_path: the place to load model
        model_name: model used to do prediction
        predict_table: where to store the tmp prediction data set
        result_table: prediction result
        model_type: type of th model, see also get_oss_saved_model_type
        model_params: parameters specified by WITH clause
        job_file: tar file incldue code and libs to execute on PAI
        params_file: extra params file
        cwd: current working dir

    Returns:
        The command to submit PAI prediction task
    """
    # NOTE(typhoonzero): for PAI machine learning toolkit predicting, we can
    # not load the TrainStmt since the model saving is fully done by PAI.
    # We directly use the columns in SELECT statement for prediction, error
    # will be reported by PAI job if the columns not match.
    conf = cluster_conf.get_cluster_config(model_params)
    conn = db.connect_with_data_source(datasource)
    if model_type == EstimatorType.PAIML:
        schema = db.get_table_schema(conn, predict_table)
        result_fields = [col[0] for col in schema]
        return ('''pai -name prediction -DmodelName="%s"  '''
                '''-DinputTableName="%s"  -DoutputTableName="%s"  '''
                '''-DfeatureColNames="%s"  -DappendColNames="%s"''') % (
                    model_name, predict_table, result_table,
                    ",".join(result_fields), ",".join(result_fields))
    else:
        schema = db.get_table_schema(conn, result_table)
        result_fields = [col[0] for col in schema]
        # For TensorFlow and XGBoost, we build a pai-tf cmd to submit the task
        return get_pai_tf_cmd(conf, job_file, params_file, ENTRY_FILE,
                              model_name, oss_model_path, predict_table, "",
                              result_table, project)


def setup_predict_entry(params, model_type):
    """Setup PAI prediction entry function according to model type"""
    if model_type == EstimatorType.TENSORFLOW:
        params["entry_type"] = "predict_tf"
    elif model_type == EstimatorType.PAIML:
        params["entry_type"] = "predict_paiml"
    elif model_type == EstimatorType.XGBOOST:
        params["entry_type"] = "predict_xgb"
    else:
        raise SQLFlowDiagnostic("unsupported model type: %d" % model_type)


def submit_pai_predict(datasource,
                       select,
                       result_table,
                       label_column,
                       model_name,
                       model_params,
                       user=""):
    """This function pack needed params and resource to a tarball
    and submit a prediction task to PAI

    Args:
        datasource: current datasource
        select: sql statement to get prediction data set
        result_table: the table name to save result
        label_column: name of the label column, if not exist in select
        model_name: model used to do prediction
        model_params: dict, Params for training, crossponding to WITH clause
    """
    params = dict(locals())

    cwd = tempfile.mkdtemp(prefix="sqlflow", dir="/tmp")
    # TODO(typhoonzero): Do **NOT** create tmp table when the select statement
    # is like: "SELECT fields,... FROM table"
    data_table = table_ops.create_tmp_table_from_select(select, datasource)
    params["data_table"] = data_table

    # format resultTable name to "db.table" to let the codegen form a
    # submitting argument of format "odps://project/tables/table_name"
    project = table_ops.get_project(datasource)
    if result_table.count(".") == 0:
        result_table = "%s.%s" % (project, result_table)

    oss_model_path = pai_model.get_oss_model_save_path(datasource,
                                                       model_name,
                                                       user=user)
    params["oss_model_path"] = oss_model_path
    model_type, estimator = pai_model.get_oss_saved_model_type_and_estimator(
        oss_model_path, project)
    setup_predict_entry(params, model_type)

    # (TODO:lhw) get train label column from model meta
    create_predict_result_table(datasource, data_table, result_table,
                                label_column, None, model_type)

    prepare_archive(cwd, estimator, oss_model_path, params)

    cmd = get_pai_predict_cmd(datasource, project, oss_model_path, model_name,
                              data_table, result_table, model_type,
                              model_params,
                              "file://" + os.path.join(cwd, JOB_ARCHIVE_FILE),
                              "file://" + os.path.join(cwd, PARAMS_FILE), cwd)
    submit_pai_task(cmd, datasource)
    table_ops.drop_tables([data_table], datasource)
