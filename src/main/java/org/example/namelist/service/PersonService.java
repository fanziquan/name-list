package org.example.namelist.service;

import org.example.namelist.entity.*;
import org.example.namelist.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 人物服务类
 * 提供正面人物和反面人物的CRUD操作
 */
@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private HeroPersonMapper heroPersonMapper;

    @Autowired
    private VillainPersonMapper villainPersonMapper;

    @Autowired
    private PersonExtendMapper personExtendMapper;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private OssService ossService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    // ==================== 字典查询操作 ====================

    /**
     * 获取正面人物分类列表（从字典表获取）
     */
    public List<Dictionary> getHeroCategories() {
        return dictionaryMapper.selectByMarkAndStatus("HERO", "1");
    }

    /**
     * 获取反面人物分类列表（从字典表获取）
     */
    public List<Dictionary> getVillainCategories() {
        return dictionaryMapper.selectByMarkAndStatus("VILLAIN", "1");
    }

    /**
     * 根据字典项获取分类名称
     */
    public String getCategoryName(String dictCode, String dictItem) {
        Dictionary dict = dictionaryMapper.selectByDictCodeAndItem(dictCode, dictItem);
        return dict != null ? dict.getItemName() : dictItem;
    }

    // ==================== 正面人物操作 ====================

    /**
     * 获取所有正面人物
     */
    public List<HeroPerson> getAllHeroes() {
        return heroPersonMapper.selectList(null);
    }

    /**
     * 根据ID获取正面人物
     */
    public HeroPerson getHeroById(String id) {
        return heroPersonMapper.selectById(id);
    }

    /**
     * 根据分类获取正面人物列表
     */
    public List<HeroPerson> getHeroesByCategory(String category) {
        return heroPersonMapper.selectByCategory(category);
    }

    /**
     * 搜索正面人物
     */
    public List<HeroPerson> searchHeroes(String keyword) {
        return heroPersonMapper.searchByName(keyword);
    }

    /**
     * 获取正面人物数量
     */
    public int getHeroCount() {
        return Math.toIntExact(heroPersonMapper.selectCount(null));
    }

    /**
     * 根据分类获取正面人物数量
     */
    public int getHeroCountByCategory(String category) {
        return heroPersonMapper.selectCountByCategory(category);
    }

    /**
     * 添加正面人物
     */
    @Transactional
    public HeroPerson addHero(HeroPerson hero) {
        // 生成ID
        String id = idGeneratorService.generateNextId(hero.getCategory());
        hero.setId(id);
        hero.setStatus(1);

        heroPersonMapper.insert(hero);
        logger.info("添加正面人物: {} - {}", id, hero.getName());
        return hero;
    }

    /**
     * 更新正面人物
     */
    @Transactional
    public boolean updateHero(HeroPerson hero) {
        int result = heroPersonMapper.updateById(hero);
        if (result > 0) {
            logger.info("更新正面人物: {}", hero.getId());
            return true;
        }
        return false;
    }

    /**
     * 删除正面人物（软删除）
     */
    @Transactional
    public boolean deleteHero(String id) {
        int result = heroPersonMapper.deleteById(id);
        if (result > 0) {
            logger.info("软删除正面人物: {}", id);
            return true;
        }
        return false;
    }

    /**
     * 物理删除正面人物
     */
    @Transactional
    public boolean physicalDeleteHero(String id) {
        HeroPerson hero = heroPersonMapper.selectById(id);
        if (hero != null && hero.getPhotoUrl() != null) {
            // 删除OSS上的照片
            ossService.deleteFile(hero.getPhotoUrl());
        }
        int result = heroPersonMapper.deleteById(id);
        if (result > 0) {
            logger.info("物理删除正面人物: {}", id);
            return true;
        }
        return false;
    }

    // ==================== 反面人物操作 ====================

    /**
     * 获取所有反面人物
     */
    public List<VillainPerson> getAllVillains() {
        return villainPersonMapper.selectList(null);
    }

    /**
     * 根据ID获取反面人物
     */
    public VillainPerson getVillainById(String id) {
        return villainPersonMapper.selectById(id);
    }

    /**
     * 根据分类获取反面人物列表
     */
    public List<VillainPerson> getVillainsByCategory(String category) {
        return villainPersonMapper.selectByCategory(category);
    }

    /**
     * 搜索反面人物
     */
    public List<VillainPerson> searchVillains(String keyword) {
        return villainPersonMapper.searchByName(keyword);
    }

    /**
     * 获取反面人物数量
     */
    public int getVillainCount() {
        return Math.toIntExact(villainPersonMapper.selectCount(null));
    }

    /**
     * 根据分类获取反面人物数量
     */
    public int getVillainCountByCategory(String category) {
        return villainPersonMapper.selectCountByCategory(category);
    }

    /**
     * 添加反面人物
     */
    @Transactional
    public VillainPerson addVillain(VillainPerson villain) {
        // 生成ID
        String id = idGeneratorService.generateNextId(villain.getCategory());
        villain.setId(id);
        villain.setStatus(1);

        villainPersonMapper.insert(villain);
        logger.info("添加反面人物: {} - {}", id, villain.getName());
        return villain;
    }

    /**
     * 更新反面人物
     */
    @Transactional
    public boolean updateVillain(VillainPerson villain) {
        int result = villainPersonMapper.updateById(villain);
        if (result > 0) {
            logger.info("更新反面人物: {}", villain.getId());
            return true;
        }
        return false;
    }

    /**
     * 删除反面人物（软删除）
     */
    @Transactional
    public boolean deleteVillain(String id) {
        int result = villainPersonMapper.deleteById(id);
        if (result > 0) {
            logger.info("软删除反面人物: {}", id);
            return true;
        }
        return false;
    }

    /**
     * 物理删除反面人物
     */
    @Transactional
    public boolean physicalDeleteVillain(String id) {
        VillainPerson villain = villainPersonMapper.selectById(id);
        if (villain != null && villain.getPhotoUrl() != null) {
            // 删除OSS上的照片
            ossService.deleteFile(villain.getPhotoUrl());
        }
        int result = villainPersonMapper.deleteById(id);
        if (result > 0) {
            logger.info("物理删除反面人物: {}", id);
            return true;
        }
        return false;
    }

    // ==================== 拓展信息操作 ====================

    /**
     * 获取正面人物的拓展信息
     */
    public PersonExtend getHeroExtend(String personId) {
        return personExtendMapper.selectById(personId);
    }

    /**
     * 获取反面人物的拓展信息
     */
    public PersonExtend getVillainExtend(String personId) {
        return personExtendMapper.selectById(personId);
    }

    /**
     * 保存拓展信息（插入或更新）
     */
    @Transactional
    public PersonExtend saveExtend(PersonExtend extend) {
        // 检查是否已存在
        PersonExtend existing = personExtendMapper.selectById(extend.getPersonId());
        if (existing != null) {
            // 存在则更新
            personExtendMapper.updateById(extend);
            logger.info("更新拓展信息: {}", extend.getPersonId());
        } else {
            // 不存在则插入
            personExtendMapper.insert(extend);
            logger.info("新增拓展信息: {}", extend.getPersonId());
        }
        return extend;
    }

    /**
     * 更新拓展信息
     */
    @Transactional
    public boolean updateExtend(PersonExtend extend) {
        int result = personExtendMapper.updateById(extend);
        if (result > 0) {
            logger.info("更新拓展信息: {}", extend.getPersonId());
            return true;
        }
        return false;
    }

    /**
     * 删除拓展信息
     */
    @Transactional
    public boolean deleteExtend(String personId) {
        int result = personExtendMapper.deleteById(personId);
        if (result > 0) {
            logger.info("删除拓展信息: {}", personId);
            return true;
        }
        return false;
    }

    // ==================== 上传照片 ====================

    /**
     * 上传人物照片
     */
    public String uploadPhoto(MultipartFile file) {
        return ossService.uploadFile(file, "photos");
    }

    /**
     * 删除人物照片
     */
    public boolean deletePhoto(String photoUrl) {
        return ossService.deleteFile(photoUrl);
    }
}
