#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#
# Copyright 2020 Alibaba Group Holding Limited. All Rights Reserved.
#
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
#

import pytest
from networkx.generators.tests.test_atlas import TestAtlasGraph
from networkx.generators.tests.test_atlas import TestAtlasGraphG

import graphscope.nx as nx
from graphscope.nx import graph_atlas
from graphscope.nx import graph_atlas_g
from graphscope.nx.utils.compat import with_graphscope_nx_context


@pytest.mark.usefixtures("graphscope_session")
@with_graphscope_nx_context(TestAtlasGraph)
class TestAtlasGraph:
    pass


@pytest.mark.usefixtures("graphscope_session")
@with_graphscope_nx_context(TestAtlasGraphG)
class TestAtlasGraphG:
    pass
