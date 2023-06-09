// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Inc.

package com.starrocks.sql.ast;

import com.starrocks.analysis.RedirectStatus;

public class EmptyStmt extends UnsupportedStmt {
    @Override
    public RedirectStatus getRedirectStatus() {
        return RedirectStatus.NO_FORWARD;
    }
}
