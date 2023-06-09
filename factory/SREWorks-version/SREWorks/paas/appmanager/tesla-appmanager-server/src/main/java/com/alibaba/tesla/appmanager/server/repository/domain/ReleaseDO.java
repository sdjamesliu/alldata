package com.alibaba.tesla.appmanager.server.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 版本发布表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 发布版本 ID
     */
    private String releaseId;

    /**
     * 发布版本名称
     */
    private String releaseName;
}