package com.alibaba.tesla.action.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * This class was generated by Ali-Generator
 * @author bang
 */
@Mapper
public interface ActionDAO {
    /**
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    long countByParam(ActionExample actionParam);

    /**
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    int deleteByParam(ActionExample actionParam);

    /**
     * @param id
     * @return
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * @param record
     * @return
     *
     * @mbg.generated
     */
    int insert(ActionDO record);

    /**
     * @param record
     * @return
     *
     * @mbg.generated
     */
    int insertSelective(ActionDO record);

    /**
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    List<ActionDO> selectByParamWithBLOBs(ActionExample actionParam);

    /**
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    List<ActionDO> selectByParam(ActionExample actionParam);

    /**
     * @param id
     * @return
     *
     * @mbg.generated
     */
    ActionDO selectByPrimaryKey(Long id);

    /**
     * @param record
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    int updateByParamSelective(@Param("record") ActionDO record, @Param("actionParam") ActionExample actionParam);

    /**
     * @param record
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    int updateByParamWithBLOBs(@Param("record") ActionDO record, @Param("actionParam") ActionExample actionParam);

    /**
     * @param record
     * @param actionParam
     * @return
     *
     * @mbg.generated
     */
    int updateByParam(@Param("record") ActionDO record, @Param("actionParam") ActionExample actionParam);

    /**
     * @param record
     * @return
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ActionDO record);

    /**
     * @param record
     * @return
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(ActionDO record);

    /**
     * @param record
     * @return
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ActionDO record);

    /**
     * @param records
     * @return
     *
     * @mbg.generated
     */
    int batchInsert(List<ActionDO> records);
}