package com.alibaba.tesla.appmanager.server.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 产品版本任务实例关联应用包任务表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReleaseTaskAppPackageTaskRelDO {
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
     * 任务 ID
     */
    private String taskId;

    /**
     * 应用包任务 ID
     */
    private Long appPackageTaskId;
}