package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.namelist.entity.AdminUser;

import java.util.List;

/**
 * 管理员用户Mapper接口
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /**
     * 根据用户名查询
     */
    AdminUser selectByUsername(@Param("username") String username);
}
