/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.metadata.model;

import java.io.Serializable;

import java.util.Locale;
import org.apache.kylin.metadata.datatype.DataType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kylin.shaded.com.google.common.base.Preconditions;

/**
 * Column Metadata from Source. All name should be uppercase.
 * <p/>
 */
@SuppressWarnings("serial")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ColumnDesc implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("datatype")
    private String datatype;

    @JsonProperty("comment")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String comment;

    @JsonProperty("data_gen")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataGen;

    @JsonProperty("index")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String index;

    @JsonProperty("cc_expr")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String computedColumnExpr = null;//if null, it's not a computed column

    // parsed from data type
    private DataType type;

    private TableDesc table;
    private int zeroBasedIndex = -1;
    private boolean isNullable = true;

    private TblColRef ref;

    // add sample min/max value
    @JsonProperty("min_value")
    private String minValue;

    @JsonProperty("max_value")
    private String maxValue;

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public ColumnDesc() { // default constructor for Jackson
    }

    public ColumnDesc(ColumnDesc other) {
        this.id = other.id;
        this.name = other.name;
        this.datatype = other.datatype;
        this.dataGen = other.datatype;
        this.comment = other.comment;
        this.dataGen = other.dataGen;
        this.index = other.index;
        this.computedColumnExpr = other.computedColumnExpr;
    }

    public ColumnDesc(String id, String name, String datatype, String comment, String dataGen, String index,
            String computedColumnExpr) {
        this.id = id;
        this.name = name;
        this.datatype = datatype;
        this.comment = comment;
        this.dataGen = dataGen;
        this.index = index;
        this.computedColumnExpr = computedColumnExpr;
    }

    /** Use TableRef.getColumn() instead */
    @Deprecated
    public TblColRef getRef() {
        if (ref == null) {
            ref = new TblColRef(this);
        }
        return ref;
    }

    public int getZeroBasedIndex() {
        return zeroBasedIndex;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        //logger.info("setting datatype to " + datatype);
        this.datatype = datatype;
        type = DataType.getType(datatype);
    }

    public DataType getUpgradedType() {
        return this.type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        if (id != null)
            zeroBasedIndex = Integer.parseInt(id) - 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableDesc getTable() {
        return table;
    }

    public void setTable(TableDesc table) {
        this.table = table;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DataType getType() {
        return type;
    }

    public String getTypeName() {
        return type.getName();
    }

    public int getTypePrecision() {
        return type.getPrecision();
    }

    public int getTypeScale() {
        return type.getScale();
    }

    public boolean isNullable() {
        return this.isNullable;
    }

    public void setNullable(boolean nullable) {
        this.isNullable = nullable;
    }

    public String getDataGen() {
        return dataGen;
    }

    public String getIndex() {
        return index;
    }

    public String getComputedColumnExpr() {
        Preconditions.checkState(computedColumnExpr != null);

        return computedColumnExpr;
    }

    public boolean isComputedColumn() {
        return computedColumnExpr != null;
    }

    public void init(TableDesc table) {
        this.table = table;

        if (name != null)
            name = name.toUpperCase(Locale.ROOT);

        if (id != null)
            zeroBasedIndex = Integer.parseInt(id) - 1;

        DataType normalized = DataType.getType(datatype);
        if (normalized == null) {
            this.setDatatype(null);
        } else {
            this.setDatatype(normalized.toString());
        }
    }

    // for test mainly
    public static ColumnDesc mockup(TableDesc table, int oneBasedColumnIndex, String name, String datatype) {
        ColumnDesc desc = new ColumnDesc();
        String id = "" + oneBasedColumnIndex;
        desc.setId(id);
        desc.setName(name);
        desc.setDatatype(datatype);
        desc.init(table);
        return desc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ColumnDesc other = (ColumnDesc) obj;

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.getIdentity().equals(other.table.getIdentity()))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "ColumnDesc{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", datatype='" + datatype + '\''
                + ", comment='" + comment + '\'' + '}';
    }
}
