package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.Dictionary;

import java.util.List;

/**
 * 数据字典Mapper接口
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {

    /**
     * 根据字典编码查询字典项列表
     */
    List<Dictionary> selectByDictCode(@Param("dictCode") String dictCode);

    /**
     * 根据字典编码和启用状态查询字典项列表
     */
    List<Dictionary> selectByDictCodeAndStatus(@Param("dictCode") String dictCode, @Param("status") String status);

    /**
     * 根据标识查询字典项列表
     */
    List<Dictionary> selectByMark(@Param("mark") String mark);

    /**
     * 根据标识和启用状态查询字典项列表
     */
    List<Dictionary> selectByMarkAndStatus(@Param("mark") String mark, @Param("status") String status);

    /**
     * 根据字典编码和字典项查询
     */
    Dictionary selectByDictCodeAndItem(@Param("dictCode") String dictCode, @Param("dictItem") String dictItem);
}
