package com.alibaba.tesla.appmanager.server.repository.condition;

import com.alibaba.tesla.appmanager.common.BaseCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 环境查询条件类
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EnvQueryCondition extends BaseCondition {

    private String namespaceId;

    private String envId;

    private String envName;

    private String envCreator;

    private String envModifier;

    /**
     * 生产环境
     */
    private Boolean production;
}
