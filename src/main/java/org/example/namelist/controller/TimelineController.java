package org.example.namelist.controller;

import org.example.namelist.entity.Event;
import org.example.namelist.entity.Period;
import org.example.namelist.entity.PersonEvent;
import org.example.namelist.service.EventService;
import org.example.namelist.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时间线和历史事件前台控制器
 * 处理前台页面的时间线和事件展示
 */
@Controller
@RequestMapping
public class TimelineController {

    @Autowired
    private EventService eventService;

    @Autowired
    private OssService ossService;

    // ==================== 前台页面 ====================

    /**
     * 历史时间线页面
     */
    @GetMapping("/timeline")
    public String timeline(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            Model model) {

        // 获取所有时期
        List<Period> periods = eventService.getAllEnabledPeriods();

        // 获取事件列表
        List<Event> events;
        if (period != null && !period.isEmpty()) {
            events = eventService.getEventsByPeriod(period);
        } else if (startYear != null && endYear != null) {
            events = eventService.getAllEnabledEvents();
            events.removeIf(e ->
                e.getEventDate() == null ||
                e.getEventDate().getYear() < startYear ||
                e.getEventDate().getYear() > endYear
            );
        } else {
            events = eventService.getAllEnabledEvents();
        }

        // 为事件生成签名URL
        for (Event event : events) {
            if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
                event.setPhotoUrl(ossService.generateSignedUrl(event.getPhotoUrl()));
            }
        }

        model.addAttribute("periods", periods);
        model.addAttribute("events", events);
        model.addAttribute("currentPeriod", period);
        model.addAttribute("startYear", startYear);
        model.addAttribute("endYear", endYear);

        return "public/timeline";
    }

    /**
     * 历史事件库页面
     */
    @GetMapping("/events")
    public String events(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String significance,
            @RequestParam(required = defaultValue = "1") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Event> events;
        int total;

        if (keyword != null && !keyword.isEmpty()) {
            events = eventService.searchEvents(keyword);
            total = events.size();
        } else if (period != null && !period.isEmpty()) {
            events = eventService.getEventsByPeriod(period);
            total = events.size();
        } else if (significance != null && !significance.isEmpty()) {
            events = eventService.getEventsBySignificance(significance);
            total = events.size();
        } else {
            events = eventService.getEventsPaged(page, 12);
            total = eventService.getEventCount();
        }

        // 为事件生成签名URL
        for (Event event : events) {
            if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
                event.setPhotoUrl(ossService.generateSignedUrl(event.getPhotoUrl()));
            }
        }

        List<Period> periods = eventService.getAllEnabledPeriods();

        model.addAttribute("events", events);
        model.addAttribute("periods", periods);
        model.addAttribute("currentPeriod", period);
        model.addAttribute("currentSignificance", significance);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalCount", total);
        model.addAttribute("totalPages", Math.max(1, (total + 11) / 12));

        return "public/events";
    }

    /**
     * 事件详情页面
     */
    @GetMapping("/event/{id}")
    public String eventDetail(@PathVariable String id, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return "redirect:/events";
        }

        // 生成签名URL
        eventService.generateSignedUrl(event);

        // 获取关联人物
        List<PersonEvent> persons = eventService.getEventPersons(id);

        model.addAttribute("event", event);
        model.addAttribute("persons", persons);

        return "public/event-detail";
    }

    // ==================== API 接口 ====================

    /**
     * API: 获取时间线数据
     */
    @ResponseBody
    @GetMapping("/api/timeline")
    public Map<String, Object> getTimelineData() {
        Map<String, Object> result = new HashMap<>();

        List<Period> periods = eventService.getAllEnabledPeriods();
        List<Event> events = eventService.getAllEnabledEvents();

        // 为事件生成签名URL
        for (Event event : events) {
            if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
                event.setPhotoUrl(ossService.generateSignedUrl(event.getPhotoUrl()));
            }
        }

        result.put("code", 200);
        result.put("data", Map.of(
            "periods", periods,
            "events", events
        ));

        return result;
    }

    /**
     * API: 获取事件列表
     */
    @ResponseBody
    @GetMapping("/api/events")
    public Map<String, Object> getEvents(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String significance,
            @RequestParam(required = defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = new HashMap<>();
        List<Event> events;

        if (period != null && !period.isEmpty()) {
            events = eventService.getEventsByPeriod(period);
        } else if (significance != null && !significance.isEmpty()) {
            events = eventService.getEventsBySignificance(significance);
        } else {
            events = eventService.getEventsPaged(page, size);
        }

        // 为事件生成签名URL
        for (Event event : events) {
            if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
                event.setPhotoUrl(ossService.generateSignedUrl(event.getPhotoUrl()));
            }
        }

        result.put("code", 200);
        result.put("data", Map.of(
            "events", events,
            "page", page,
            "size", size
        ));

        return result;
    }

    /**
     * API: 获取事件详情
     */
    @ResponseBody
    @GetMapping("/api/event/{id}")
    public Map<String, Object> getEventDetail(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();

        Event event = eventService.getEventDetail(id);
        if (event == null) {
            result.put("code", 404);
            result.put("message", "事件不存在");
            return result;
        }

        // 生成签名URL
        eventService.generateSignedUrl(event);

        result.put("code", 200);
        result.put("data", event);

        return result;
    }

    /**
     * API: 获取事件的参与人物
     */
    @ResponseBody
    @GetMapping("/api/event/{id}/persons")
    public Map<String, Object> getEventPersons(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        List<PersonEvent> persons = eventService.getEventPersons(id);

        result.put("code", 200);
        result.put("data", persons);

        return result;
    }

    /**
     * API: 获取人物参与的事件
     */
    @ResponseBody
    @GetMapping("/api/person/{personId}/events")
    public Map<String, Object> getPersonEvents(
            @PathVariable String personId,
            @RequestParam(defaultValue = "HERO") String personType) {

        Map<String, Object> result = new HashMap<>();
        List<PersonEvent> events = eventService.getPersonEvents(personId, personType);

        result.put("code", 200);
        result.put("data", events);

        return result;
    }
}
