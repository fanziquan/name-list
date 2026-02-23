package org.example.namelist.controller;

import org.example.namelist.entity.Dictionary;
import org.example.namelist.entity.HeroPerson;
import org.example.namelist.entity.PersonExtend;
import org.example.namelist.entity.VillainPerson;
import org.example.namelist.service.OssService;
import org.example.namelist.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台公共控制器
 * 处理前台页面的展示逻辑
 */
@Controller
public class PublicController {

    @Autowired
    private PersonService personService;

    @Autowired
    private OssService ossService;

    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // 从字典表获取正面人物分类
        List<Dictionary> heroDicts = personService.getHeroCategories();
        List<Map<String, Object>> heroCategoryList = new ArrayList<>();
        for (Dictionary dict : heroDicts) {
            Map<String, Object> catInfo = new HashMap<>();
            catInfo.put("code", dict.getDictItem());
            catInfo.put("name", dict.getItemName());
            catInfo.put("count", personService.getHeroCountByCategory(dict.getDictItem()));
            heroCategoryList.add(catInfo);
        }

        // 从字典表获取反面人物分类
        List<Dictionary> villainDicts = personService.getVillainCategories();
        List<Map<String, Object>> villainCategoryList = new ArrayList<>();
        for (Dictionary dict : villainDicts) {
            Map<String, Object> catInfo = new HashMap<>();
            catInfo.put("code", dict.getDictItem());
            catInfo.put("name", dict.getItemName());
            catInfo.put("count", personService.getVillainCountByCategory(dict.getDictItem()));
            villainCategoryList.add(catInfo);
        }

        model.addAttribute("heroCategories", heroCategoryList);
        model.addAttribute("villainCategories", villainCategoryList);

        return "public/index";
    }

    /**
     * 正面人物列表
     */
    @GetMapping("/hero")
    public String heroList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<HeroPerson> heroes;

        if (keyword != null && !keyword.isEmpty()) {
            // 搜索
            heroes = personService.searchHeroes(keyword);
        } else if (category != null && !category.isEmpty()) {
            // 按分类筛选
            heroes = personService.getHeroesByCategory(category);
        } else {
            // 获取所有
            heroes = personService.getAllHeroes();
        }

        // 获取分类列表
        List<Dictionary> categories = personService.getHeroCategories();

        // 构建分类名称 Map
        Map<String, String> categoryNames = new HashMap<>();
        for (Dictionary cat : categories) {
            categoryNames.put(cat.getDictItem(), cat.getItemName());
        }

        model.addAttribute("heroes", heroes);
        model.addAttribute("categories", categories);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("currentCategory", category);
        model.addAttribute("keyword", keyword);

        return "public/hero-list";
    }

    /**
     * 反面人物列表
     */
    @GetMapping("/villain")
    public String villainList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<VillainPerson> villains;

        if (keyword != null && !keyword.isEmpty()) {
            // 搜索
            villains = personService.searchVillains(keyword);
        } else if (category != null && !category.isEmpty()) {
            // 按分类筛选
            villains = personService.getVillainsByCategory(category);
        } else {
            // 获取所有
            villains = personService.getAllVillains();
        }

        // 获取分类列表
        List<Dictionary> categories = personService.getVillainCategories();

        // 构建分类名称 Map
        Map<String, String> categoryNames = new HashMap<>();
        for (Dictionary cat : categories) {
            categoryNames.put(cat.getDictItem(), cat.getItemName());
        }

        model.addAttribute("villains", villains);
        model.addAttribute("categories", categories);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("currentCategory", category);
        model.addAttribute("keyword", keyword);

        return "public/villain-list";
    }

    /**
     * 正面人物详情
     */
    @GetMapping("/hero/{id}")
    public String heroDetail(@PathVariable String id, Model model) {
        HeroPerson hero = personService.getHeroById(id);
        if (hero == null) {
            return "redirect:/hero";
        }

        // 获取拓展信息
        PersonExtend extend = personService.getHeroExtend(id);

        // 获取分类名称
        String categoryName = personService.getCategoryName("CATEGORY", hero.getCategory());

        // 如果有照片URL，生成签名URL（用于访问私有OSS文件）
        String photoUrl = hero.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            hero.setPhotoUrl(ossService.generateSignedUrl(photoUrl));
        }

        model.addAttribute("hero", hero);
        model.addAttribute("extend", extend);
        model.addAttribute("categoryName", categoryName);

        return "public/hero-detail";
    }

    /**
     * 反面人物详情
     */
    @GetMapping("/villain/{id}")
    public String villainDetail(@PathVariable String id, Model model) {
        VillainPerson villain = personService.getVillainById(id);
        if (villain == null) {
            return "redirect:/villain";
        }

        // 获取拓展信息
        PersonExtend extend = personService.getVillainExtend(id);

        // 获取分类名称
        String categoryName = personService.getCategoryName("CATEGORY", villain.getCategory());

        // 如果有照片URL，生成签名URL（用于访问私有OSS文件）
        String photoUrl = villain.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            villain.setPhotoUrl(ossService.generateSignedUrl(photoUrl));
        }

        model.addAttribute("villain", villain);
        model.addAttribute("extend", extend);
        model.addAttribute("categoryName", categoryName);

        return "public/villain-detail";
    }

    /**
     * 搜索
     */
    @GetMapping("/search")
    public String search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "all") String type,
            Model model) {

        List<HeroPerson> heroResults = null;
        List<VillainPerson> villainResults = null;

        if ("all".equals(type) || "hero".equals(type)) {
            heroResults = personService.searchHeroes(keyword);
        }
        if ("all".equals(type) || "villain".equals(type)) {
            villainResults = personService.searchVillains(keyword);
        }

        // 获取分类名称
        List<Dictionary> heroCategories = personService.getHeroCategories();
        List<Dictionary> villainCategories = personService.getVillainCategories();

        Map<String, String> heroCategoryNames = new HashMap<>();
        for (Dictionary cat : heroCategories) {
            heroCategoryNames.put(cat.getDictItem(), cat.getItemName());
        }

        Map<String, String> villainCategoryNames = new HashMap<>();
        for (Dictionary cat : villainCategories) {
            villainCategoryNames.put(cat.getDictItem(), cat.getItemName());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", type);
        model.addAttribute("heroResults", heroResults);
        model.addAttribute("villainResults", villainResults);
        model.addAttribute("heroCategoryNames", heroCategoryNames);
        model.addAttribute("villainCategoryNames", villainCategoryNames);

        return "public/search";
    }

    /**
     * API: 获取正面人物列表
     */
    @ResponseBody
    @GetMapping("/api/hero")
    public Map<String, Object> getHeroes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> result = new HashMap<>();
        List<HeroPerson> heroes;

        if (keyword != null && !keyword.isEmpty()) {
            heroes = personService.searchHeroes(keyword);
        } else if (category != null && !category.isEmpty()) {
            heroes = personService.getHeroesByCategory(category);
        } else {
            heroes = personService.getAllHeroes();
        }

        // 为照片URL生成签名
        for (HeroPerson hero : heroes) {
            String photoUrl = hero.getPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                hero.setPhotoUrl(ossService.generateSignedUrl(photoUrl));
            }
        }

        result.put("code", 200);
        result.put("data", heroes);
        return result;
    }

    /**
     * API: 获取反面人物列表
     */
    @ResponseBody
    @GetMapping("/api/villain")
    public Map<String, Object> getVillains(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> result = new HashMap<>();
        List<VillainPerson> villains;

        if (keyword != null && !keyword.isEmpty()) {
            villains = personService.searchVillains(keyword);
        } else if (category != null && !category.isEmpty()) {
            villains = personService.getVillainsByCategory(category);
        } else {
            villains = personService.getAllVillains();
        }

        result.put("code", 200);
        result.put("data", villains);
        return result;
    }
}
