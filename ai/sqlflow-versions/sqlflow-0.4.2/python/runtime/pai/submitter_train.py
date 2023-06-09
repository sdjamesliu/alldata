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

from runtime.pai import cluster_conf, pai_model, table_ops
from runtime.pai.get_pai_tf_cmd import (ENTRY_FILE, JOB_ARCHIVE_FILE,
                                        PARAMS_FILE, get_pai_tf_cmd)
from runtime.pai.pai_ml.kmeans import get_train_kmeans_pai_cmd
from runtime.pai.pai_ml.random_forest import get_train_random_forest_pai_cmd
from runtime.pai.prepare_archive import prepare_archive
from runtime.pai.submit_pai_task import submit_pai_task


def get_pai_train_cmd(datasource, estimator_string, model_name, train_table,
                      val_table, model_params, train_params, path_to_save,
                      job_file, params_file, cwd):
    """Get train model comman for PAI

    Args:
        datasource: current datasource
        estimator_string: estimator name, Keras class name, or XGBoost
        model_name: the model name to train
        train_table: data table from which to load train data
        val_table: data table from which to load evaluate data
        model_params: params for training, crossponding to WITH clause
        train_params: parmas for the trainning process
        path_to_save: path to save the model
        job_file: tar file incldue code and libs to execute on PAI
        params_file: extra params file
        cwd: current working dir

    Returns:
        The command to submit a PAI train task
    """
    project = table_ops.get_project(datasource)
    conf = cluster_conf.get_cluster_config(model_params)
    if estimator_string.lower() == "randomforests":
        cmd = get_train_random_forest_pai_cmd(
            model_name, train_table, model_params,
            train_params["feature_column_names"],
            train_params["label_meta"]["feature_name"])
    elif estimator_string.lower() == "kmeans":
        cmd = get_train_kmeans_pai_cmd(datasource, model_name, train_table,
                                       model_params,
                                       train_params["feature_column_names"])
    else:
        cmd = get_pai_tf_cmd(conf, job_file, params_file, ENTRY_FILE,
                             model_name, path_to_save, train_table, val_table,
                             "", project)
    return cmd


# TODO(lhw): adapt this interface after we do feature derivation in Python
def submit_pai_train(datasource, estimator_string, select, validation_select,
                     model_params, save, load, **train_params):
    """This function submit PAI-TF train task to PAI platform

    Args:
        datasource: string
            Like: odps://access_id:access_key@service.com/api?
                         curr_project=test_ci&scheme=http
        estimator_string: string
            TensorFlow estimator name, Keras class name, or XGBoost
        select: string
            The SQL statement for selecting data for train
        validation_select: string
            Ths SQL statement for selecting data for validation
        model_params: dict
            Params for training, crossponding to WITH clause
        load: string
            The pre-trained model name to load
        train_params: dict
            Extra train params, will be passed to runtime.tensorflow.train.
    """
    # prepare params for tensorflow train,
    # the params will be pickled into train_params.pkl
    params = dict(locals())
    del params["train_params"]
    params.update(train_params)

    if estimator_string.lower().startswith("xgboost"):
        params["entry_type"] = "train_xgb"
    else:
        params["entry_type"] = "train_tf"

    cwd = tempfile.mkdtemp(prefix="sqlflow", dir="/tmp")

    train_table, val_table = table_ops.create_train_and_eval_tmp_table(
        select, validation_select, datasource)
    params["pai_table"], params["pai_val_table"] = train_table, val_table

    # FIXME(typhoonzero): get user from session
    user = ""
    if "user" in params:
        user = params["user"]

    # clean target dir
    path_to_save = pai_model.get_oss_model_save_path(datasource,
                                                     save,
                                                     user=user)
    path_to_load = pai_model.get_oss_model_save_path(datasource,
                                                     load,
                                                     user=user)
    params["oss_model_dir"] = path_to_save

    if path_to_load == "" or path_to_load != path_to_save:
        pai_model.clean_oss_model_path(path_to_save + "/")

    # zip all required resource to a tarball
    prepare_archive(cwd, estimator_string, path_to_save, params)

    # submit pai task to execute the training
    cmd = get_pai_train_cmd(datasource, estimator_string, save, train_table,
                            val_table, model_params, train_params,
                            path_to_save,
                            "file://" + os.path.join(cwd, JOB_ARCHIVE_FILE),
                            "file://" + os.path.join(cwd, PARAMS_FILE), cwd)

    submit_pai_task(cmd, datasource)

    # save trained model to sqlfs
    pai_model.save_model_to_sqlfs(datasource, path_to_save, save)
    table_ops.drop_tables([train_table, val_table], datasource)
