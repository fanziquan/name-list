package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.PersonExtend;

/**
 * 人物拓展信息Mapper接口
 */
@Mapper
public interface PersonExtendMapper extends BaseMapper<PersonExtend> {

    /**
     * 根据人物ID更新
     */
    int updateByPersonId(PersonExtend personExtend);
}
