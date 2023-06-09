// Copyright (c) 2021 Terminus, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pipelineymlvars

// envs
const (
	CLUSTER_NAME = "CLUSTER_NAME"
)

type YmlField string

func (f YmlField) String() string {
	return string(f)
}

const (
	FieldAggregate           YmlField = "aggregate"
	FieldGet                 YmlField = "get"
	FieldPut                 YmlField = "put"
	FieldTask                YmlField = "task"
	FieldDisable             YmlField = "disable"
	FieldPause               YmlField = "pause"
	FieldEnvs                YmlField = "envs"
	FieldParams              YmlField = "params"
	FieldParamForceBuildpack YmlField = "force_buildpack"
	FieldTaskConfig          YmlField = "config"
	FieldTaskConfigEnvs      YmlField = "envs"
)
