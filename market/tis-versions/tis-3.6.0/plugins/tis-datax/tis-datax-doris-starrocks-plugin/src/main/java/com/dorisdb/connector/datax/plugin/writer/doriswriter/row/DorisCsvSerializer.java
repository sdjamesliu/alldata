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

import java.io.StringWriter;

import com.alibaba.datax.common.element.Record;

import com.google.common.base.Strings;

public class DorisCsvSerializer extends DorisBaseSerializer implements DorisISerializer {

    private static final long serialVersionUID = 1L;

    private final String columnSeparator;

    public DorisCsvSerializer(String sp) {
        this.columnSeparator = DorisDelimiterParser.parse(sp, "\t");
    }

    @Override
    public String serialize(Record row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getColumnNumber(); i++) {
            String value = fieldConvertion(row.getColumn(i));
            sb.append(null == value ? "\\N" : value);
            if (i < row.getColumnNumber() - 1) {
                sb.append(columnSeparator);
            }
        }
        return sb.toString();
    }

}
