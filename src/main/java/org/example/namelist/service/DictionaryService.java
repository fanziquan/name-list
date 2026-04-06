package org.example.namelist.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.namelist.entity.Dictionary;
import org.example.namelist.mapper.DictionaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 数据字典服务类
 * 提供数据字典的CRUD操作
 */
@Service
public class DictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);

    @Autowired
    private DictionaryMapper dictionaryMapper;

    /**
     * 获取所有字典数据
     */
    public List<Dictionary> getAllDictionaries() {
        return dictionaryMapper.selectList(null);
    }

    /**
     * 根据字典编码获取字典项列表
     */
    public List<Dictionary> getByDictCode(String dictCode) {
        return dictionaryMapper.selectByDictCode(dictCode);
    }

    /**
     * 根据字典编码和状态获取字典项列表
     */
    public List<Dictionary> getByDictCodeAndStatus(String dictCode, String status) {
        return dictionaryMapper.selectByDictCodeAndStatus(dictCode, status);
    }

    /**
     * 根据标识获取字典项列表
     */
    public List<Dictionary> getByMark(String mark) {
        return dictionaryMapper.selectByMark(mark);
    }

    /**
     * 根据字典编码和字典项获取字典项
     */
    public Dictionary getByDictCodeAndItem(String dictCode, String dictItem) {
        return dictionaryMapper.selectByDictCodeAndItem(dictCode, dictItem);
    }

    /**
     * 获取所有字典编码（去重）
     */
    public List<String> getAllDictCodes() {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Dictionary::getDictCode, Dictionary::getDictName)
              .groupBy(Dictionary::getDictCode, Dictionary::getDictName)
              .orderByAsc(Dictionary::getDictCode);
        List<Dictionary> list = dictionaryMapper.selectList(wrapper);
        return list.stream().map(Dictionary::getDictCode).toList();
    }

    /**
     * 添加字典项
     */
    @Transactional
    public boolean add(Dictionary dictionary) {
        try {
            // 检查是否已存在相同的字典编码和字典项
            Dictionary existing = getByDictCodeAndItem(dictionary.getDictCode(), dictionary.getDictItem());
            if (existing != null) {
                logger.warn("字典项已存在: dictCode={}, dictItem={}", dictionary.getDictCode(), dictionary.getDictItem());
                return false;
            }
            dictionary.setCreateTime(new Date());
            dictionary.setUpdateTime(new Date());
            if (dictionary.getStatus() == null) {
                dictionary.setStatus("1"); // 默认启用
            }
            return dictionaryMapper.insert(dictionary) > 0;
        } catch (Exception e) {
            logger.error("添加字典项失败", e);
            return false;
        }
    }

    /**
     * 更新字典项
     */
    @Transactional
    public boolean update(Dictionary dictionary) {
        try {
            dictionary.setUpdateTime(new Date());
            // 使用 dictCode + dictItem 作为条件更新
            LambdaUpdateWrapper<Dictionary> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Dictionary::getDictCode, dictionary.getDictCode())
                   .eq(Dictionary::getDictItem, dictionary.getDictItem())
                   .set(Dictionary::getDictName, dictionary.getDictName())
                   .set(Dictionary::getItemName, dictionary.getItemName())
                   .set(Dictionary::getMark, dictionary.getMark())
                   .set(Dictionary::getStatus, dictionary.getStatus())
                   .set(Dictionary::getUpdateTime, new Date());
            return dictionaryMapper.update(wrapper) > 0;
        } catch (Exception e) {
            logger.error("更新字典项失败", e);
            return false;
        }
    }

    /**
     * 根据字典编码和字典项删除
     */
    @Transactional
    public boolean deleteByCodeAndItem(String dictCode, String dictItem) {
        try {
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dictionary::getDictCode, dictCode)
                   .eq(Dictionary::getDictItem, dictItem);
            return dictionaryMapper.delete(wrapper) > 0;
        } catch (Exception e) {
            logger.error("删除字典项失败", e);
            return false;
        }
    }

    /**
     * 批量删除字典项
     */
    @Transactional
    public int batchDelete(List<String> codes) {
        try {
            // 使用字典编码批量删除
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Dictionary::getDictCode, codes);
            return dictionaryMapper.delete(wrapper);
        } catch (Exception e) {
            logger.error("批量删除字典项失败", e);
            return 0;
        }
    }
}
