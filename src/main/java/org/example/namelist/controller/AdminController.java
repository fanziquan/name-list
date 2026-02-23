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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 * 处理后台管理的增删改查操作
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PersonService personService;

    @Autowired
    private OssService ossService;

    /**
     * 后台首页（仪表盘）
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // 获取统计数据
        int heroCount = personService.getHeroCount();
        int villainCount = personService.getVillainCount();

        // 获取最新的人物
        List<HeroPerson> recentHeroes = personService.getAllHeroes();
        List<VillainPerson> recentVillains = personService.getAllVillains();

        // 限制显示数量
        if (recentHeroes.size() > 5) {
            recentHeroes = recentHeroes.subList(0, 5);
        }
        if (recentVillains.size() > 5) {
            recentVillains = recentVillains.subList(0, 5);
        }

        // 获取所有分类名称
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

        model.addAttribute("heroCount", heroCount);
        model.addAttribute("villainCount", villainCount);
        model.addAttribute("recentHeroes", recentHeroes);
        model.addAttribute("recentVillains", recentVillains);
        model.addAttribute("heroCategoryNames", heroCategoryNames);
        model.addAttribute("villainCategoryNames", villainCategoryNames);

        return "admin/dashboard";
    }

    // ==================== 正面人物管理 ====================

    /**
     * 正面人物列表页
     */
    @GetMapping("/hero/list")
    public String heroList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<HeroPerson> heroes;

        if (keyword != null && !keyword.isEmpty()) {
            heroes = personService.searchHeroes(keyword);
        } else if (category != null && !category.isEmpty()) {
            heroes = personService.getHeroesByCategory(category);
        } else {
            heroes = personService.getAllHeroes();
        }

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

        return "admin/hero-manage";
    }

    /**
     * 添加正面人物页面
     */
    @GetMapping("/hero/add")
    public String addHero(Model model) {
        List<Dictionary> categories = personService.getHeroCategories();
        model.addAttribute("categories", categories);
        return "admin/hero-form";
    }

    /**
     * 编辑正面人物页面
     */
    @GetMapping("/hero/edit/{id}")
    public String editHero(@PathVariable String id, Model model) {
        HeroPerson hero = personService.getHeroById(id);
        if (hero == null) {
            return "redirect:/admin/hero/list";
        }

        // 对图片URL进行签名处理（解决私有bucket访问问题）
        if (hero.getPhotoUrl() != null && !hero.getPhotoUrl().isEmpty()) {
            hero.setPhotoUrl(ossService.generateSignedUrl(hero.getPhotoUrl()));
        }

        List<Dictionary> categories = personService.getHeroCategories();
        PersonExtend extend = personService.getHeroExtend(id);

        model.addAttribute("hero", hero);
        model.addAttribute("extend", extend);
        model.addAttribute("categories", categories);

        return "admin/hero-form";
    }

    /**
     * 保存正面人物
     */
    @ResponseBody
    @PostMapping("/hero/save")
    public Map<String, Object> saveHero(
            @ModelAttribute HeroPerson hero,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 如果有新照片上传
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = personService.uploadPhoto(photo);
                hero.setPhotoUrl(photoUrl);
            }

            if (hero.getId() == null || hero.getId().isEmpty()) {
                // 新增
                personService.addHero(hero);
                result.put("code", 200);
                result.put("message", "添加成功");
            } else {
                // 更新
                personService.updateHero(hero);
                result.put("code", 200);
                result.put("message", "更新成功");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除正面人物
     */
    @ResponseBody
    @PostMapping("/hero/delete/{id}")
    public Map<String, Object> deleteHero(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = personService.deleteHero(id);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 500);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    // ==================== 反面人物管理 ====================

    /**
     * 反面人物列表页
     */
    @GetMapping("/villain/list")
    public String villainList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<VillainPerson> villains;

        if (keyword != null && !keyword.isEmpty()) {
            villains = personService.searchVillains(keyword);
        } else if (category != null && !category.isEmpty()) {
            villains = personService.getVillainsByCategory(category);
        } else {
            villains = personService.getAllVillains();
        }

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

        return "admin/villain-manage";
    }

    /**
     * 添加反面人物页面
     */
    @GetMapping("/villain/add")
    public String addVillain(Model model) {
        List<Dictionary> categories = personService.getVillainCategories();
        model.addAttribute("categories", categories);
        return "admin/villain-form";
    }

    /**
     * 编辑反面人物页面
     */
    @GetMapping("/villain/edit/{id}")
    public String editVillain(@PathVariable String id, Model model) {
        VillainPerson villain = personService.getVillainById(id);
        if (villain == null) {
            return "redirect:/admin/villain/list";
        }

        // 对图片URL进行签名处理（解决私有bucket访问问题）
        if (villain.getPhotoUrl() != null && !villain.getPhotoUrl().isEmpty()) {
            villain.setPhotoUrl(ossService.generateSignedUrl(villain.getPhotoUrl()));
        }

        List<Dictionary> categories = personService.getVillainCategories();
        PersonExtend extend = personService.getVillainExtend(id);

        model.addAttribute("villain", villain);
        model.addAttribute("extend", extend);
        model.addAttribute("categories", categories);

        return "admin/villain-form";
    }

    /**
     * 保存反面人物
     */
    @ResponseBody
    @PostMapping("/villain/save")
    public Map<String, Object> saveVillain(
            @ModelAttribute VillainPerson villain,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 如果有新照片上传
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = personService.uploadPhoto(photo);
                villain.setPhotoUrl(photoUrl);
            }

            if (villain.getId() == null || villain.getId().isEmpty()) {
                // 新增
                personService.addVillain(villain);
                result.put("code", 200);
                result.put("message", "添加成功");
            } else {
                // 更新
                personService.updateVillain(villain);
                result.put("code", 200);
                result.put("message", "更新成功");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除反面人物
     */
    @ResponseBody
    @PostMapping("/villain/delete/{id}")
    public Map<String, Object> deleteVillain(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = personService.deleteVillain(id);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 500);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    // ==================== 拓展信息管理 ====================

    /**
     * 保存拓展信息
     */
    @ResponseBody
    @PostMapping("/extend/save")
    public Map<String, Object> saveExtend(@RequestBody PersonExtend extend) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 保存拓展信息（插入或更新）
            personService.saveExtend(extend);

            result.put("code", 200);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    // ==================== 上传接口 ====================

    /**
     * 上传照片
     */
    @ResponseBody
    @PostMapping("/upload/photo")
    public Map<String, Object> uploadPhoto(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            String photoUrl = personService.uploadPhoto(file);
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("url", photoUrl);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }
}
