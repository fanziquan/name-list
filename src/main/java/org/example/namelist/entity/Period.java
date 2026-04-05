package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 历史时期实体类
 */
@TableName("period")
public class Period implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键: 时期编码，如 "MODERN" */
    @TableId(type = IdType.INPUT)
    private String code;

    /** 时期名称，如 "近代史" */
    private String name;

    /** 开始年份 */
    private Integer startYear;

    /** 结束年份 */
    private Integer endYear;

    /** 排序号 */
    private Integer orderNum;

    /** 时期简介 */
    private String description;

    /** 状态: 1-启用, 0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== Getters and Setters ====================

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * 获取年份区间字符串
     */
    public String getYearRange() {
        if (startYear != null && endYear != null) {
            return startYear + " - " + endYear;
        } else if (startYear != null) {
            return startYear + " - ";
        } else if (endYear != null) {
            return " - " + endYear;
        }
        return "";
    }
}
