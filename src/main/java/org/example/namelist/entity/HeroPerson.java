package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 正面人物实体类
 */
@TableName("hero_person")
public class HeroPerson implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键: 分类代码(3位) + 自增数字(5位) */
    @TableId(type = IdType.INPUT)
    private String id;

    /** 姓名 */
    private String name;

    /** 出生年份 */
    private Integer birthYear;

    /** 逝世年份 */
    private Integer deathYear;

    /** 分类: MAR(烈士)/SCI(科学家)/GEN(将军)/PAT(爱国志士) */
    private String category;

    /** 国籍 */
    private String nationality;

    /** 简要介绍(200字内) */
    private String briefIntro;

    /** 完整事迹(大段描述) */
    private String fullBio;

    /** 照片URL(OSS) */
    private String photoUrl;

    /** 拓展信息ID */
    private String extendId;

    /** 状态: 1-显示, 0-隐藏 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== Getters and Setters ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }

    public String getFullBio() {
        return fullBio;
    }

    public void setFullBio(String fullBio) {
        this.fullBio = fullBio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getExtendId() {
        return extendId;
    }

    public void setExtendId(String extendId) {
        this.extendId = extendId;
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
     * 获取生卒年份字符串
     */
    public String getLifeSpan() {
        if (birthYear != null && deathYear != null) {
            return birthYear + " - " + deathYear;
        } else if (birthYear != null) {
            return birthYear + " - ";
        } else if (deathYear != null) {
            return " - " + deathYear;
        }
        return "";
    }

    /**
     * 获取分类名称
     */
    public String getCategoryName() {
        if (category == null) return "";
        switch (category) {
            case "MAR": return "烈士";
            case "SCI": return "科学家";
            case "GEN": return "将军";
            case "PAT": return "爱国志士";
            default: return category;
        }
    }
}
