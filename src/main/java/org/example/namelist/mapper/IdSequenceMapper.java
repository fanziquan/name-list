package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.IdSequence;

/**
 * ID序列Mapper接口
 */
@Mapper
public interface IdSequenceMapper extends BaseMapper<IdSequence> {

    /**
     * 更新当前值
     */
    int updateCurrentValue(@Param("categoryCode") String categoryCode, @Param("currentValue") Integer currentValue);
}
