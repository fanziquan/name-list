package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人物拓展信息实体类
 */
@TableName("person_extend")
public class PersonExtend implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 关联人物ID (作为主键) */
    @TableId(type = IdType.INPUT)
    private String personId;

    /** 人物类型: HERO/VILLAIN */
    private String personType;

    /** 主要成就 */
    private String achievements;

    /** 历史评价 */
    private String evaluation;

    /** 逸闻趣事 */
    private String anecdotes;

    /** 资料来源 */
    private String sources;

    /** 扩展字段1 */
    private String extraField1;

    /** 扩展字段2 */
    private String extraField2;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== Getters and Setters ====================

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

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getAnecdotes() {
        return anecdotes;
    }

    public void setAnecdotes(String anecdotes) {
        this.anecdotes = anecdotes;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getExtraField1() {
        return extraField1;
    }

    public void setExtraField1(String extraField1) {
        this.extraField1 = extraField1;
    }

    public String getExtraField2() {
        return extraField2;
    }

    public void setExtraField2(String extraField2) {
        this.extraField2 = extraField2;
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
}
