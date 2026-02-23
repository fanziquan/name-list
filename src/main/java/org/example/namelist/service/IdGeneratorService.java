package org.example.namelist.service;

import org.example.namelist.entity.IdSequence;
import org.example.namelist.mapper.IdSequenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ID生成器服务
 * 用于生成分类ID: 三位分类代码 + 五位自增数字
 * 例如: MAR00001, SCI00001, TRA00001
 */
@Service
public class IdGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorService.class);

    @Autowired
    private IdSequenceMapper idSequenceMapper;

    /**
     * 获取下一个ID
     *
     * @param categoryCode 分类代码 (如 MAR, SCI, TRA 等)
     * @return 生成的ID字符串
     */
    @Transactional
    public String generateNextId(String categoryCode) {
        // 检查分类代码是否存在
        IdSequence sequence = idSequenceMapper.selectById(categoryCode);
        if (sequence == null) {
            // 如果不存在，初始化该分类
            sequence = new IdSequence();
            sequence.setCategoryCode(categoryCode);
            sequence.setCurrentValue(0);
            idSequenceMapper.insert(sequence);
            logger.info("初始化分类代码: {}", categoryCode);
            return String.format("%s%05d", categoryCode, 1);
        }

        // 增加当前值
        Integer newValue = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(newValue);
        idSequenceMapper.updateById(sequence);

        // 生成ID: 分类代码 + 5位自增数字
        String id = String.format("%s%05d", categoryCode, newValue);

        logger.debug("生成ID: {} -> {}", categoryCode, id);

        return id;
    }

    /**
     * 获取当前ID值（不生成新ID）
     *
     * @param categoryCode 分类代码
     * @return 当前ID字符串，如果不存在则返回null
     */
    public String getCurrentId(String categoryCode) {
        IdSequence sequence = idSequenceMapper.selectById(categoryCode);
        if (sequence == null || sequence.getCurrentValue() == null) {
            return null;
        }
        return String.format("%s%05d", categoryCode, sequence.getCurrentValue());
    }

    /**
     * 重置指定分类的序列值
     *
     * @param categoryCode 分类代码
     * @param newValue    新的起始值
     */
    @Transactional
    public void resetSequence(String categoryCode, int newValue) {
        idSequenceMapper.updateCurrentValue(categoryCode, newValue);
        logger.info("重置分类代码 {} 的序列值为: {}", categoryCode, newValue);
    }
}
