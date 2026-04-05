package org.example.namelist.controller;

import org.example.namelist.entity.Event;
import org.example.namelist.entity.HeroPerson;
import org.example.namelist.entity.Period;
import org.example.namelist.entity.PersonEvent;
import org.example.namelist.entity.VillainPerson;
import org.example.namelist.service.EventService;
import org.example.namelist.service.OssService;
import org.example.namelist.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史事件管理控制器
 * 处理后台管理的历史事件、时期、关联管理
 */
@Controller
@RequestMapping("/admin/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OssService ossService;

    // ==================== 时期管理 ====================

    /**
     * 时期列表页
     */
    @GetMapping("/period/list")
    public String periodList(Model model) {
        List<Period> periods = eventService.getAllEnabledPeriods();
        model.addAttribute("periods", periods);
        return "admin/period-manage";
    }

    /**
     * 添加时期页面
     */
    @GetMapping("/period/add")
    public String addPeriod(Model model) {
        return "admin/period-form";
    }

    /**
     * 编辑时期页面
     */
    @GetMapping("/period/edit/{code}")
    public String editPeriod(@PathVariable String code, Model model) {
        Period period = eventService.getPeriodByCode(code);
        if (period == null) {
            return "redirect:/admin/event/period/list";
        }
        model.addAttribute("period", period);
        return "admin/period-form";
    }

    /**
     * 保存时期
     */
    @ResponseBody
    @PostMapping("/period/save")
    public Map<String, Object> savePeriod(@ModelAttribute Period period) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (period.getCode() == null || period.getCode().isEmpty()) {
                // 新增（但时期编码不允许新增）
                result.put("code", 500);
                result.put("message", "时期编码不能为空");
            } else {
                // 检查编码是否已存在
                Period existing = eventService.getPeriodByCode(period.getCode());
                if (existing == null) {
                    result.put("code", 500);
                    result.put("message", "时期不存在，无法更新");
                } else {
                    eventService.updatePeriod(period);
                    result.put("code", 200);
                    result.put("message", "保存成功");
                }
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "保存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除时期
     */
    @ResponseBody
    @PostMapping("/period/delete/{code}")
    public Map<String, Object> deletePeriod(@PathVariable String code) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = eventService.deletePeriod(code);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
            } else {
                result.put("code", 500);
                result.put("message", "删除失败：时期下存在关联事件");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    // ==================== 事件管理 ====================

    /**
     * 事件列表页
     */
    @GetMapping("/list")
    public String eventList(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String significance,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {

        List<Event> events;
        int total;

        if (keyword != null && !keyword.isEmpty()) {
            events = eventService.searchEvents(keyword);
            total = events.size();
        } else {
            events = eventService.getEventsPaged(page, 10);
            total = eventService.getEventCount();
        }

        List<Period> periods = eventService.getAllEnabledPeriods();

        model.addAttribute("events", events);
        model.addAttribute("periods", periods);
        model.addAttribute("currentPeriod", period);
        model.addAttribute("currentSignificance", significance);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalCount", total);
        model.addAttribute("totalPages", (total + 9) / 10);

        return "admin/event-manage";
    }

    /**
     * 添加事件页面
     */
    @GetMapping("/add")
    public String addEvent(Model model) {
        List<Period> periods = eventService.getAllEnabledPeriods();
        model.addAttribute("periods", periods);
        return "admin/event-form";
    }

    /**
     * 编辑事件页面
     */
    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable String id, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return "redirect:/admin/event/list";
        }

        // 生成签名URL
        eventService.generateSignedUrl(event);

        List<Period> periods = eventService.getAllEnabledPeriods();
        List<PersonEvent> persons = eventService.getEventPersons(id);

        model.addAttribute("event", event);
        model.addAttribute("periods", periods);
        model.addAttribute("persons", persons);

        return "admin/event-form";
    }

    /**
     * 保存事件
     */
    @ResponseBody
    @PostMapping("/save")
    public Map<String, Object> saveEvent(
            @ModelAttribute Event event,
            @RequestParam(required = false) MultipartFile photo) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 如果有新照片上传
            if (photo != null && !photo.isEmpty()) {
                String photoUrl = eventService.uploadPhoto(photo);
                event.setPhotoUrl(photoUrl);
            }

            if (event.getId() == null || event.getId().isEmpty()) {
                // 生成ID
                String id = eventService.generateEventId();
                event.setId(id);
                eventService.addEvent(event);
                result.put("code", 200);
                result.put("message", "添加成功");
            } else {
                // 更新
                eventService.updateEvent(event);
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
     * 删除事件
     */
    @ResponseBody
    @PostMapping("/delete/{id}")
    public Map<String, Object> deleteEvent(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();

        try {
            Event event = eventService.getEventById(id);
            boolean success = eventService.deleteEvent(id);
            if (success) {
                // 删除OSS上的照片
                if (event != null && event.getPhotoUrl() != null) {
                    ossService.deleteFile(event.getPhotoUrl());
                }
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

    // ==================== 人物-事件关联管理 ====================

    /**
     * 关联列表页
     */
    @GetMapping("/relation/list")
    public String relationList(
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {

        List<PersonEvent> relations;
        int total;

        if (keyword != null && !keyword.isEmpty()) {
            // 简单搜索
            relations = eventService.getAllPersonEvents(1, 100);
            // 过滤
            final String kw = keyword.toLowerCase();
            relations.removeIf(r ->
                (r.getPersonName() == null || !r.getPersonName().toLowerCase().contains(kw)) &&
                (r.getEventName() == null || !r.getEventName().toLowerCase().contains(kw))
            );
            total = relations.size();
        } else if (eventId != null && !eventId.isEmpty()) {
            relations = eventService.getEventPersons(eventId);
            total = relations.size();
        } else {
            relations = eventService.getAllPersonEvents(page, 10);
            total = eventService.getPersonEventCount();
        }

        List<Event> events = eventService.getAllEnabledEvents();

        model.addAttribute("relations", relations);
        model.addAttribute("events", events);
        model.addAttribute("currentEventId", eventId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalCount", total);
        model.addAttribute("totalPages", Math.max(1, (total + 9) / 10));

        return "admin/event-relation-manage";
    }

    /**
     * 添加关联页面
     */
    @GetMapping("/relation/add")
    public String addRelation(Model model) {
        List<Event> events = eventService.getAllEnabledEvents();
        List<HeroPerson> heroes = personService.getAllHeroes();
        List<VillainPerson> villains = personService.getAllVillains();

        model.addAttribute("events", events);
        model.addAttribute("heroes", heroes);
        model.addAttribute("villains", villains);

        return "admin/event-relation-form";
    }

    /**
     * 编辑关联页面
     */
    @GetMapping("/relation/edit/{id}")
    public String editRelation(@PathVariable Integer id, Model model) {
        PersonEvent relation = eventService.getPersonEventById(id);
        
        if (relation == null) {
            return "redirect:/admin/event/relation/list";
        }

        List<Event> events = eventService.getAllEnabledEvents();
        List<HeroPerson> heroes = personService.getAllHeroes();
        List<VillainPerson> villains = personService.getAllVillains();

        model.addAttribute("relation", relation);
        model.addAttribute("events", events);
        model.addAttribute("heroes", heroes);
        model.addAttribute("villains", villains);

        return "admin/event-relation-form";
    }

    /**
     * 保存关联
     */
    @ResponseBody
    @PostMapping("/relation/save")
    public Map<String, Object> saveRelation(@ModelAttribute PersonEvent relation) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (relation.getId() == null) {
                // 新增
                PersonEvent added = eventService.addPersonEvent(relation);
                if (added == null) {
                    result.put("code", 500);
                    result.put("message", "该人物与事件已存在关联");
                } else {
                    result.put("code", 200);
                    result.put("message", "添加成功");
                }
            } else {
                // 更新
                eventService.updatePersonEvent(relation);
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
     * 删除关联
     */
    @ResponseBody
    @PostMapping("/relation/delete/{id}")
    public Map<String, Object> deleteRelation(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = eventService.deletePersonEvent(id);
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

    // ==================== 辅助方法 ====================

    /**
     * 上传照片
     */
    public String uploadPhoto(MultipartFile file) {
        return ossService.uploadFile(file, "events");
    }
}
