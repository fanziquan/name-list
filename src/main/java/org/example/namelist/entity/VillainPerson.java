package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 反面人物实体类
 */
@TableName("villain_person")
public class VillainPerson implements Serializable {
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

    /** 分类: TRA(汉奸)/PUB(公知)/ESP(间谍)/COR(贪官) */
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
            case "TRA": return "汉奸";
            case "PUB": return "公知";
            case "ESP": return "间谍";
            case "COR": return "贪官";
            default: return category;
        }
    }
}
