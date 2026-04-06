package org.example.namelist.controller;

import org.example.namelist.entity.Dictionary;
import org.example.namelist.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典管理控制器
 */
@Controller
@RequestMapping("/admin/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 字典列表页
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String dictCode,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        List<Dictionary> dictionaries;
        List<String> dictCodes = dictionaryService.getAllDictCodes();
        
        if (keyword != null && !keyword.isEmpty()) {
            // 关键词搜索
            dictionaries = dictionaryService.getAllDictionaries();
            final String kw = keyword.toLowerCase();
            dictionaries.removeIf(d ->
                (d.getDictName() == null || !d.getDictName().toLowerCase().contains(kw)) &&
                (d.getDictCode() == null || !d.getDictCode().toLowerCase().contains(kw)) &&
                (d.getDictItem() == null || !d.getDictItem().toLowerCase().contains(kw)) &&
                (d.getItemName() == null || !d.getItemName().toLowerCase().contains(kw))
            );
        } else if (dictCode != null && !dictCode.isEmpty()) {
            // 按字典编码筛选
            dictionaries = dictionaryService.getByDictCode(dictCode);
        } else {
            dictionaries = dictionaryService.getAllDictionaries();
        }
        
        model.addAttribute("dictionaries", dictionaries);
        model.addAttribute("dictCodes", dictCodes);
        model.addAttribute("currentDictCode", dictCode);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", "dictionary");
        
        return "admin/dictionary-manage";
    }

    /**
     * 添加字典页
     */
    @GetMapping("/add")
    public String add(Model model) {
        List<String> dictCodes = dictionaryService.getAllDictCodes();
        model.addAttribute("dictCodes", dictCodes);
        model.addAttribute("currentPage", "dictionary");
        return "admin/dictionary-form";
    }

    /**
     * 编辑字典页（通过 dictCode + dictItem 查询）
     */
    @GetMapping("/edit")
    public String edit(
            @RequestParam String dictCode,
            @RequestParam String dictItem,
            Model model) {
        Dictionary dictionary = dictionaryService.getByDictCodeAndItem(dictCode, dictItem);
        if (dictionary == null) {
            return "redirect:/admin/dictionary/list";
        }
        List<String> dictCodes = dictionaryService.getAllDictCodes();
        model.addAttribute("dictionary", dictionary);
        model.addAttribute("dictCodes", dictCodes);
        model.addAttribute("currentPage", "dictionary");
        return "admin/dictionary-form";
    }

    /**
     * 保存字典（dictCode + dictItem 联合主键）
     */
    @ResponseBody
    @PostMapping("/save")
    public Map<String, Object> save(Dictionary dictionary) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查字典项是否已存在
            Dictionary existing = dictionaryService.getByDictCodeAndItem(
                dictionary.getDictCode(), dictionary.getDictItem());
            
            if (existing != null) {
                // 已存在则更新
                existing.setDictName(dictionary.getDictName());
                existing.setItemName(dictionary.getItemName());
                existing.setMark(dictionary.getMark());
                existing.setStatus(dictionary.getStatus());
                boolean success = dictionaryService.update(existing);
                if (success) {
                    result.put("code", 200);
                    result.put("message", "更新成功");
                } else {
                    result.put("code", 500);
                    result.put("message", "更新失败");
                }
            } else {
                // 不存在则新增
                boolean success = dictionaryService.add(dictionary);
                if (success) {
                    result.put("code", 200);
                    result.put("message", "添加成功");
                } else {
                    result.put("code", 500);
                    result.put("message", "添加失败");
                }
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除字典
     */
    @ResponseBody
    @PostMapping("/delete")
    public Map<String, Object> delete(
            @RequestParam String dictCode,
            @RequestParam String dictItem) {
        Map<String, Object> result = new HashMap<>();
        try {
            Dictionary dictionary = dictionaryService.getByDictCodeAndItem(dictCode, dictItem);
            if (dictionary == null) {
                result.put("code", 500);
                result.put("message", "字典项不存在");
                return result;
            }
            boolean success = dictionaryService.deleteByCodeAndItem(dictCode, dictItem);
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

    /**
     * 切换状态
     */
    @ResponseBody
    @PostMapping("/toggle")
    public Map<String, Object> toggleStatus(
            @RequestParam String dictCode,
            @RequestParam String dictItem) {
        Map<String, Object> result = new HashMap<>();
        try {
            Dictionary dictionary = dictionaryService.getByDictCodeAndItem(dictCode, dictItem);
            if (dictionary == null) {
                result.put("code", 500);
                result.put("message", "字典项不存在");
                return result;
            }
            boolean success = dictionaryService.toggleStatusByCodeAndItem(dictCode, dictItem);
            if (success) {
                result.put("code", 200);
                result.put("message", "状态切换成功");
            } else {
                result.put("code", 500);
                result.put("message", "状态切换失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }
}
