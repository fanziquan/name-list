package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员用户实体类
 */
@TableName("admin_user")
public class AdminUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户名 */
    private String username;

    /** 密码(Bcrypt加密) */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 角色: ADMIN/SUPER_ADMIN */
    private String role;

    /** 状态: 1-正常, 0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== Getters and Setters ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // ==================== Helper Methods ====================

    /**
     * 判断是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    /**
     * 判断用户是否可用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
