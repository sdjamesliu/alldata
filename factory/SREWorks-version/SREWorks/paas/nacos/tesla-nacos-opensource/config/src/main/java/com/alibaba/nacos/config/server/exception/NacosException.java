/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.nacos.config.server.exception;

/**
 * Nacos exception
 *
 * @author Nacos
 */
public class NacosException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3913902031489277776L;

    private int errCode;

    private String errMsg;

    public NacosException() {
    }

    public NacosException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "ErrCode:" + errCode + ",ErrMsg:" + errMsg;
    }

    /**
     * server error code, use http code 400 403 throw exception to user 500 502
     * 503 change ip and retry
     */
    /**
     * invalid param（参数错误）
     */
    public static final int INVALID_PARAM = 400;
    /**
     * no right（鉴权失败）
     */
    public static final int NO_RIGHT = 403;
    /**
     * conflict（写并发冲突）
     */
    public static final int CONFLICT = 409;
    /**
     * server error（server异常，如超时）
     */
    public static final int SERVER_ERROR = 500;
    /**
     * bad gateway（路由异常，如nginx后面的Server挂掉）
     */
    public static final int BAD_GATEWAY = 502;
    /**
     * over threshold（超过server端的限流阈值）
     */
    public static final int OVER_THRESHOLD = 503;

}
