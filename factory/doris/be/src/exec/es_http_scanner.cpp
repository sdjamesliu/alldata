// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

#include "exec/es_http_scanner.h"

#include <iostream>
#include <sstream>

#include "exprs/expr.h"
#include "exprs/expr_context.h"
#include "runtime/descriptors.h"
#include "runtime/runtime_state.h"
#include "runtime/tuple.h"

namespace doris {

EsHttpScanner::EsHttpScanner(RuntimeState* state, RuntimeProfile* profile, TupleId tuple_id,
                             const std::map<std::string, std::string>& properties,
                             const std::vector<ExprContext*>& conjunct_ctxs, EsScanCounter* counter,
                             bool doc_value_mode)
        : _state(state),
          _profile(profile),
          _tuple_id(tuple_id),
          _properties(properties),
          _conjunct_ctxs(conjunct_ctxs),
          _next_range(0),
          _line_eof(false),
          _batch_eof(false),
          _mem_pool(new MemPool()),
          _tuple_desc(nullptr),
          _counter(counter),
          _es_reader(nullptr),
          _es_scroll_parser(nullptr),
          _doc_value_mode(doc_value_mode),
          _rows_read_counter(nullptr),
          _read_timer(nullptr),
          _materialize_timer(nullptr) {}

EsHttpScanner::~EsHttpScanner() {
    close();
}

Status EsHttpScanner::open() {
    _tuple_desc = _state->desc_tbl().get_tuple_descriptor(_tuple_id);
    if (_tuple_desc == nullptr) {
        return Status::InternalError("Unknown tuple descriptor, tuple_id={}", _tuple_id);
    }

    const std::string& host = _properties.at(ESScanReader::KEY_HOST_PORT);
    _es_reader.reset(new ESScanReader(host, _properties, _doc_value_mode));
    if (_es_reader == nullptr) {
        return Status::InternalError("Es reader construct failed.");
    }

    RETURN_IF_ERROR(_es_reader->open());

    _rows_read_counter = ADD_COUNTER(_profile, "RowsRead", TUnit::UNIT);
    _read_timer = ADD_TIMER(_profile, "TotalRawReadTime(*)");
    _materialize_timer = ADD_TIMER(_profile, "MaterializeTupleTime(*)");

    return Status::OK();
}

Status EsHttpScanner::get_next(Tuple* tuple, MemPool* tuple_pool, bool* eof,
                               const std::map<std::string, std::string>& docvalue_context) {
    return Status::InternalError("es not support non-vec engine");
}

void EsHttpScanner::close() {
    if (_es_reader != nullptr) {
        _es_reader->close();
    }

    Expr::close(_conjunct_ctxs, _state);
}

} // namespace doris
