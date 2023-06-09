package com.alibaba.tesla.authproxy.model.vo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储 Aliyun Refresh Token 的对象
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliyunRefreshTokenVO {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private String expiresIn;
}
