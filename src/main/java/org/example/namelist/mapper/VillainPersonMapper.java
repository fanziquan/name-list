package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.VillainPerson;

import java.util.List;

/**
 * 反面人物Mapper接口
 */
@Mapper
public interface VillainPersonMapper extends BaseMapper<VillainPerson> {

    /**
     * 根据分类查询
     */
    List<VillainPerson> selectByCategory(@Param("category") String category);

    /**
     * 根据姓名搜索
     */
    List<VillainPerson> searchByName(@Param("name") String name);

    /**
     * 根据分类查询数量
     */
    int selectCountByCategory(@Param("category") String category);
}
