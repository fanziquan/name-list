package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.Event;

import java.util.List;

/**
 * 历史事件Mapper接口
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {

    /**
     * 根据时期查询
     */
    List<Event> selectByPeriod(@Param("periodCode") String periodCode);

    /**
     * 根据重要程度查询
     */
    List<Event> selectBySignificance(@Param("significance") String significance);

    /**
     * 搜索事件
     */
    List<Event> searchByName(@Param("keyword") String keyword);

    /**
     * 获取启用的事件（分页）
     */
    List<Event> selectEnabledPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取启用事件总数
     */
    int selectEnabledCount();

    /**
     * 根据时期获取启用事件总数
     */
    int selectCountByPeriod(@Param("periodCode") String periodCode);

    /**
     * 根据重要程度获取启用事件总数
     */
    int selectCountBySignificance(@Param("significance") String significance);
}
