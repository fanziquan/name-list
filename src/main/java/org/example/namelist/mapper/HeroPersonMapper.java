package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.HeroPerson;

import java.util.List;

/**
 * 正面人物Mapper接口
 */
@Mapper
public interface HeroPersonMapper extends BaseMapper<HeroPerson> {

    /**
     * 根据分类查询
     */
    List<HeroPerson> selectByCategory(@Param("category") String category);

    /**
     * 根据姓名搜索
     */
    List<HeroPerson> searchByName(@Param("name") String name);

    /**
     * 根据分类查询数量
     */
    int selectCountByCategory(@Param("category") String category);
}
