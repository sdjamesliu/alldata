/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.dorisdb.connector.datax.plugin.writer.doriswriter.row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.datax.common.element.Record;
import com.alibaba.fastjson.JSON;

public class DorisJsonSerializer extends DorisBaseSerializer implements DorisISerializer {

    private static final long serialVersionUID = 1L;

    private final List<String> fieldNames;

    public DorisJsonSerializer(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public String serialize(Record row) {
        if (null == fieldNames) {
            return "";
        }
        Map<String, Object> rowMap = new HashMap<>(fieldNames.size());
        int idx = 0;
        for (String fieldName : fieldNames) {
            rowMap.put(fieldName, fieldConvertion(row.getColumn(idx)));
            idx++;
        }
        return JSON.toJSONString(rowMap);
    }

}
