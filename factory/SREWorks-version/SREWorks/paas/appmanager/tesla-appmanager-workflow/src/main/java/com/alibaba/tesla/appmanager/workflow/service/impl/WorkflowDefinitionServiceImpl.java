package com.alibaba.tesla.appmanager.workflow.service.impl;

import com.alibaba.tesla.appmanager.common.pagination.Pagination;
import com.alibaba.tesla.appmanager.workflow.repository.condition.WorkflowDefinitionQueryCondition;
import com.alibaba.tesla.appmanager.workflow.repository.domain.WorkflowDefinitionDO;
import com.alibaba.tesla.appmanager.workflow.service.WorkflowDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Workflow 定义服务
 *
 * @author yaoxing.gyx@alibaba-inc.com
 */
@Service
@Slf4j
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    /**
     * 根据指定条件查询对应的 WorkflowDefinition 列表
     *
     * @param condition 条件
     * @param operator  操作人
     * @return Page of WorkflowDefinition
     */
    @Override
    public Pagination<WorkflowDefinitionDO> list(WorkflowDefinitionQueryCondition condition, String operator) {
        return null;
    }

    /**
     * 根据指定条件查询对应的 WorkflowDefinition (期望只返回一个)
     *
     * @param condition 条件
     * @param operator  操作人
     * @return Page of WorkflowDefinition
     */
    @Override
    public WorkflowDefinitionDO get(WorkflowDefinitionQueryCondition condition, String operator) {
        return null;
    }

    /**
     * 向系统中新增或更新一个 WorkflowDefinition
     *
     * @param request  记录的值
     * @param operator 操作人
     */
    @Override
    public void apply(WorkflowDefinitionDO request, String operator) {

    }

    /**
     * 删除指定条件的 WorkflowDefinition (必须传入 WorkflowType 参数明确删除对象)
     *
     * @param condition 条件
     * @param operator  操作人
     * @return 删除的数量 (0 or 1)
     */
    @Override
    public int delete(WorkflowDefinitionQueryCondition condition, String operator) {
        return 0;
    }
}
