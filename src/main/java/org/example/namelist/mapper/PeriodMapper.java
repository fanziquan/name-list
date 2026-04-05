package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.Period;

import java.util.List;

/**
 * 历史时期Mapper接口
 */
@Mapper
public interface PeriodMapper extends BaseMapper<Period> {

    /**
     * 获取所有启用的时期（按排序号）
     */
    List<Period> selectAllEnabled();

    /**
     * 根据编码查询
     */
    Period selectByCode(@Param("code") String code);
}
