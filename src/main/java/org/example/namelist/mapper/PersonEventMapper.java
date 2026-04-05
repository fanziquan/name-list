package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.PersonEvent;

import java.util.List;

/**
 * 人物-事件关联Mapper接口
 */
@Mapper
public interface PersonEventMapper extends BaseMapper<PersonEvent> {

    /**
     * 根据事件ID查询关联人物
     */
    List<PersonEvent> selectByEventId(@Param("eventId") String eventId);

    /**
     * 根据人物ID查询关联事件
     */
    List<PersonEvent> selectByPersonId(@Param("personId") String personId);

    /**
     * 根据事件ID统计人物数量
     */
    int countByEventId(@Param("eventId") String eventId);

    /**
     * 根据人物ID统计事件数量
     */
    int countByPersonId(@Param("personId") String personId);

    /**
     * 检查关联是否已存在
     */
    int existsByPersonAndEvent(@Param("personId") String personId, @Param("eventId") String eventId);

    /**
     * 根据人物ID和类型查询关联事件
     */
    List<PersonEvent> selectByPersonIdAndType(@Param("personId") String personId, @Param("personType") String personType);
}
