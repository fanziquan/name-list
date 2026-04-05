package org.example.namelist.service;

import org.example.namelist.entity.Event;
import org.example.namelist.entity.Period;
import org.example.namelist.entity.PersonEvent;
import org.example.namelist.mapper.EventMapper;
import org.example.namelist.mapper.PeriodMapper;
import org.example.namelist.mapper.PersonEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 历史事件服务类
 * 提供历史事件和时期的 CRUD 操作
 */
@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private PersonEventMapper personEventMapper;

    @Autowired
    private OssService ossService;

    // ==================== 时期操作 ====================

    /**
     * 获取所有启用的时期
     */
    public List<Period> getAllEnabledPeriods() {
        return periodMapper.selectAllEnabled();
    }

    /**
     * 根据编码获取时期
     */
    public Period getPeriodByCode(String code) {
        return periodMapper.selectByCode(code);
    }

    /**
     * 添加时期
     */
    @Transactional
    public Period addPeriod(Period period) {
        period.setStatus(1);
        period.setCreateTime(LocalDateTime.now());
        period.setUpdateTime(LocalDateTime.now());
        periodMapper.insert(period);
        logger.info("添加时期: {} - {}", period.getCode(), period.getName());
        return period;
    }

    /**
     * 更新时期
     */
    @Transactional
    public boolean updatePeriod(Period period) {
        period.setUpdateTime(LocalDateTime.now());
        int result = periodMapper.updateById(period);
        if (result > 0) {
            logger.info("更新时期: {}", period.getCode());
            return true;
        }
        return false;
    }

    /**
     * 删除时期
     */
    @Transactional
    public boolean deletePeriod(String code) {
        // 检查是否有事件关联
        int eventCount = eventMapper.selectCountByPeriod(code);
        if (eventCount > 0) {
            logger.warn("无法删除时期 {}，仍有 {} 个事件关联", code, eventCount);
            return false;
        }
        int result = periodMapper.deleteById(code);
        if (result > 0) {
            logger.info("删除时期: {}", code);
            return true;
        }
        return false;
    }

    // ==================== 事件操作 ====================

    /**
     * 获取所有事件
     */
    public List<Event> getAllEvents() {
        return eventMapper.selectList(null);
    }

    /**
     * 获取所有启用的事件
     */
    public List<Event> getAllEnabledEvents() {
        return eventMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Event>()
                .eq("status", 1)
                .orderByAsc("event_date")
        );
    }

    /**
     * 根据ID获取事件
     */
    public Event getEventById(String id) {
        return eventMapper.selectById(id);
    }

    /**
     * 根据时期获取事件
     */
    public List<Event> getEventsByPeriod(String periodCode) {
        return eventMapper.selectByPeriod(periodCode);
    }

    /**
     * 根据重要程度获取事件
     */
    public List<Event> getEventsBySignificance(String significance) {
        return eventMapper.selectBySignificance(significance);
    }

    /**
     * 搜索事件
     */
    public List<Event> searchEvents(String keyword) {
        return eventMapper.searchByName(keyword);
    }

    /**
     * 分页获取事件
     */
    public List<Event> getEventsPaged(int page, int size) {
        int offset = (page - 1) * size;
        return eventMapper.selectEnabledPaged(offset, size);
    }

    /**
     * 获取事件总数
     */
    public int getEventCount() {
        return eventMapper.selectEnabledCount();
    }

    /**
     * 添加事件
     */
    @Transactional
    public Event addEvent(Event event) {
        event.setStatus(1);
        event.setCreateTime(LocalDateTime.now());
        event.setUpdateTime(LocalDateTime.now());
        eventMapper.insert(event);
        logger.info("添加事件: {} - {}", event.getId(), event.getName());
        return event;
    }

    /**
     * 更新事件
     */
    @Transactional
    public boolean updateEvent(Event event) {
        event.setUpdateTime(LocalDateTime.now());
        int result = eventMapper.updateById(event);
        if (result > 0) {
            logger.info("更新事件: {}", event.getId());
            return true;
        }
        return false;
    }

    /**
     * 删除事件
     */
    @Transactional
    public boolean deleteEvent(String id) {
        // 删除关联
        personEventMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PersonEvent>()
                .eq("event_id", id)
        );
        // 删除事件
        int result = eventMapper.deleteById(id);
        if (result > 0) {
            logger.info("删除事件: {}", id);
            return true;
        }
        return false;
    }

    /**
     * 获取事件详情（含关联人物）
     */
    public Event getEventDetail(String id) {
        Event event = eventMapper.selectById(id);
        if (event != null) {
            // 获取关联人物
            List<PersonEvent> persons = personEventMapper.selectByEventId(id);
            event.setPersons(persons);
            // 获取时期名称
            Period period = periodMapper.selectByCode(event.getPeriodCode());
            if (period != null) {
                event.setPeriodName(period.getName());
            }
            // 获取人物数量
            event.setPersonCount(persons.size());
        }
        return event;
    }

    /**
     * 为事件照片生成签名URL
     */
    public void generateSignedUrl(Event event) {
        if (event.getPhotoUrl() != null && !event.getPhotoUrl().isEmpty()) {
            event.setPhotoUrl(ossService.generateSignedUrl(event.getPhotoUrl()));
        }
    }

    // ==================== 人物-事件关联操作 ====================

    /**
     * 获取事件的参与人物
     */
    public List<PersonEvent> getEventPersons(String eventId) {
        List<PersonEvent> persons = personEventMapper.selectByEventId(eventId);
        // 为照片生成签名URL
        for (PersonEvent pe : persons) {
            if (pe.getPersonPhotoUrl() != null && !pe.getPersonPhotoUrl().isEmpty()) {
                pe.setPersonPhotoUrl(ossService.generateSignedUrl(pe.getPersonPhotoUrl()));
            }
        }
        return persons;
    }

    /**
     * 获取人物的参与事件
     */
    public List<PersonEvent> getPersonEvents(String personId, String personType) {
        return personEventMapper.selectByPersonIdAndType(personId, personType);
    }

    /**
     * 添加人物-事件关联
     */
    @Transactional
    public PersonEvent addPersonEvent(PersonEvent personEvent) {
        // 检查是否已存在
        if (personEventMapper.existsByPersonAndEvent(personEvent.getPersonId(), personEvent.getEventId()) > 0) {
            logger.warn("关联已存在: person={}, event={}", personEvent.getPersonId(), personEvent.getEventId());
            return null;
        }
        personEvent.setCreateTime(LocalDateTime.now());
        personEventMapper.insert(personEvent);
        logger.info("添加人物-事件关联: person={}, event={}", personEvent.getPersonId(), personEvent.getEventId());
        return personEvent;
    }

    /**
     * 更新人物-事件关联
     */
    @Transactional
    public boolean updatePersonEvent(PersonEvent personEvent) {
        int result = personEventMapper.updateById(personEvent);
        if (result > 0) {
            logger.info("更新人物-事件关联: {}", personEvent.getId());
            return true;
        }
        return false;
    }

    /**
     * 删除人物-事件关联
     */
    @Transactional
    public boolean deletePersonEvent(Integer id) {
        int result = personEventMapper.deleteById(id);
        if (result > 0) {
            logger.info("删除人物-事件关联: {}", id);
            return true;
        }
        return false;
    }

    /**
     * 获取关联列表（分页）
     */
    public List<PersonEvent> getAllPersonEvents(int page, int size) {
        int offset = (page - 1) * size;
        return personEventMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PersonEvent>()
                .orderByDesc("create_time")
                .last("LIMIT " + offset + ", " + size)
        );
    }

    /**
     * 获取关联总数
     */
    public int getPersonEventCount() {
        return Math.toIntExact(personEventMapper.selectCount(null));
    }

    // ==================== 时间线数据 ====================

    /**
     * 获取时间线数据
     */
    public List<Event> getTimelineData() {
        return eventMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Event>()
                .eq("status", 1)
                .orderByAsc("event_date")
        );
    }

    // ==================== ID生成 ====================

    private static final AtomicInteger eventIdCounter = new AtomicInteger(1);

    /**
     * 生成事件ID
     */
    public String generateEventId() {
        return String.format("EVT%05d", eventIdCounter.getAndIncrement());
    }

    // ==================== 文件上传 ====================

    /**
     * 上传事件照片
     */
    public String uploadPhoto(MultipartFile file) {
        return ossService.uploadFile(file, "events");
    }
}
