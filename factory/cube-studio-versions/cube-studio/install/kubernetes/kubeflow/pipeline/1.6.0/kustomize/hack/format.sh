#!/bin/bash
#
# Copyright 2020 The Kubeflow Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -ex

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null && pwd)"

function format_yaml {
    local path=$1
    local tmp=$(mktemp)
    yq r "$path" > "$tmp"
    cp "$tmp" "$path"
}
echo "This formatting script uses yq, it can be downloaded at https://github.com/mikefarah/yq/releases/tag/3.3.0"
kustomization_yamls_with_images=(
  "base/cache-deployer/kustomization.yaml"
  "base/cache/kustomization.yaml"
  "base/metadata/base/kustomization.yaml"
  "base/pipeline/metadata-writer/kustomization.yaml"
  "base/pipeline/kustomization.yaml"
  "env/gcp/inverse-proxy/kustomization.yaml"
)
for path in "${kustomization_yamls_with_images[@]}"
do
  format_yaml "$DIR/../$path"
done
