# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
.PHONY: presto_protocol presto_protocol-json presto_protocol-cpp

default: presto_protocol

presto_protocol: presto_protocol-cpp

presto_protocol-cpp: presto_protocol-json
	echo "// DO NOT EDIT : This file is generated by chevron" > presto_protocol.cpp
	chevron -d presto_protocol.json presto_protocol-json-cpp.mustache >> presto_protocol.cpp
	echo "// DO NOT EDIT : This file is generated by chevron" > presto_protocol.h
	chevron -d presto_protocol.json presto_protocol-json-hpp.mustache >> presto_protocol.h
	clang-format -style=file -i presto_protocol.h presto_protocol.cpp
	
presto_protocol-json: 
	./java-to-struct-json.py special/*.java special/*.inc -j | jq . > presto_protocol.json

presto_protocol.proto: presto_protocol-json
	pystache presto_protocol-protobuf.mustache presto_protocol.json > presto_protocol.proto
