package com.alibaba.tesla.appmanager.server.repository.condition;

import com.alibaba.tesla.appmanager.common.BaseCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 产品发布版本引用表的查询条件
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReleaseRelQueryCondition extends BaseCondition {

    private String productId;

    private String releaseId;

    private String appPackageTag;
}
