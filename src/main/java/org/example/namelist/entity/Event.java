package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 历史事件实体类
 */
@TableName("event")
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键: 事件ID，如 "EVT001" */
    @TableId(type = IdType.INPUT)
    private String id;

    /** 事件名称 */
    private String name;

    /** 事件日期 */
    private LocalDate eventDate;

    /** 所属时期编码 */
    private String periodCode;

    /** 事件地点 */
    private String location;

    /** 简要描述（100字内） */
    private String briefDesc;

    /** 详细描述 */
    private String fullDesc;

    /** 重要程度: MAJOR-重大, ORDINARY-普通 */
    private String significance;

    /** 事件配图URL */
    private String photoUrl;

    /** 状态: 1-显示, 0-隐藏 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 非数据库字段：关联人物列表 */
    @TableField(exist = false)
    private List<PersonEvent> persons;

    /** 非数据库字段：时期名称 */
    @TableField(exist = false)
    private String periodName;

    /** 非数据库字段：参与人物数量 */
    @TableField(exist = false)
    private Integer personCount;

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

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getFullDesc() {
        return fullDesc;
    }

    public void setFullDesc(String fullDesc) {
        this.fullDesc = fullDesc;
    }

    public String getSignificance() {
        return significance;
    }

    public void setSignificance(String significance) {
        this.significance = significance;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public List<PersonEvent> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonEvent> persons) {
        this.persons = persons;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }

    // ==================== Helper Methods ====================

    /**
     * 获取格式化日期
     */
    public String getFormattedDate() {
        if (eventDate != null) {
            return eventDate.getYear() + "年" + eventDate.getMonthValue() + "月" + eventDate.getDayOfMonth() + "日";
        }
        return "";
    }

    /**
     * 获取重要程度名称
     */
    public String getSignificanceName() {
        if ("MAJOR".equals(significance)) {
            return "重大";
        } else if ("ORDINARY".equals(significance)) {
            return "普通";
        }
        return significance;
    }
}
