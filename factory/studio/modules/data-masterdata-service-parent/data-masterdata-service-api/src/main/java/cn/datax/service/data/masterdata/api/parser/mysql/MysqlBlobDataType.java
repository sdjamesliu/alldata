package cn.datax.service.data.masterdata.api.parser.mysql;

import cn.datax.service.data.masterdata.api.parser.DataType;

public class MysqlBlobDataType implements DataType {

    @Override
    public String fillTypeString(String columnLength, String columnScale) {
        return "blob";
    }
}
