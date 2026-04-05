package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人物-事件关联实体类
 */
@TableName("person_event")
public class PersonEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键: 自增ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 人物ID（如 "MAR00001"） */
    private String personId;

    /** 人物类型: HERO-正面人物, VILLAIN-反面人物 */
    private String personType;

    /** 事件ID */
    private String eventId;

    /** 在事件中的角色描述，如 "总指挥" */
    private String roleDesc;

    /** 贡献描述 */
    private String contribution;

    /** 创建时间 */
    private LocalDateTime createTime;

    // 非数据库字段：人物信息
    @TableField(exist = false)
    private String personName;

    @TableField(exist = false)
    private String personPhotoUrl;

    @TableField(exist = false)
    private String personCategory;

    // 非数据库字段：事件信息
    @TableField(exist = false)
    private String eventName;

    @TableField(exist = false)
    private String eventDate;

    // ==================== Getters and Setters ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPhotoUrl() {
        return personPhotoUrl;
    }

    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.personPhotoUrl = personPhotoUrl;
    }

    public String getPersonCategory() {
        return personCategory;
    }

    public void setPersonCategory(String personCategory) {
        this.personCategory = personCategory;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    // ==================== Helper Methods ====================

    /**
     * 获取人物类型名称
     */
    public String getPersonTypeName() {
        if ("HERO".equals(personType)) {
            return "英雄";
        } else if ("VILLAIN".equals(personType)) {
            return "反派";
        }
        return personType;
    }
}
