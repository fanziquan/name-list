package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据字典实体类
 */
@Data
@TableName("dictionary")
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    private String dictCode;

    /** 字典中文名称 */
    private String dictName;

    /** 字典项(编码) */
    private String dictItem;

    /** 字典项中文名 */
    private String itemName;

    /** 标识: HERO(正面人物)/VILLAIN(反面人物) */
    private String mark;

    /** 是否启用: 1-启用, 0-禁用 */
    private String status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

}
