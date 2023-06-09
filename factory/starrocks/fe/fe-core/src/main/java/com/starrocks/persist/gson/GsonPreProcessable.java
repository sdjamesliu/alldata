// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Inc.

package com.starrocks.persist.gson;

import java.io.IOException;

public interface GsonPreProcessable {
    public void gsonPreProcess() throws IOException;
}
