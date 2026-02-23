package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ID序列实体类 (用于ID生成器)
 */
@TableName("id_sequence")
public class IdSequence implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分类代码 */
    @TableId(type = IdType.INPUT)
    private String categoryCode;

    /** 当前最大值 */
    private Integer currentValue;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== Getters and Setters ====================

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // ==================== Helper Methods ====================

    /**
     * 获取下一个ID
     */
    public String getNextId() {
        if (currentValue == null) {
            currentValue = 0;
        }
        currentValue++;
        return String.format("%s%05d", categoryCode, currentValue);
    }
}
