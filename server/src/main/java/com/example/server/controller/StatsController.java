package com.example.server.controller;

import com.example.server.entity.User;
import com.example.server.service.HealthService;
import com.example.server.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器：为管理后台首页图表提供数据
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final UserService userService;
    private final HealthService healthService;

    public StatsController(UserService userService, HealthService healthService) {
        this.userService = userService;
        this.healthService = healthService;
    }

    /**
     * 用户统计：近 7 天注册趋势 + 用户来源分布
     */
    @GetMapping("/users")
    public Map<String, Object> users() {
        List<User> users = userService.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");

        // 近 7 天每日注册数
        List<String> dates = new ArrayList<>();
        List<Long> userCounts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            dates.add(day.format(fmt));
            long count = users.stream()
                    .filter(u -> u.getCreatedAt() != null
                            && u.getCreatedAt().toLocalDate().equals(day))
                    .count();
            userCounts.add(count);
        }

        // 用户来源分布（按是否绑定手机号简单分类）
        long withPhone = users.stream()
                .filter(u -> u.getPhone() != null && !u.getPhone().isBlank())
                .count();
        long withoutPhone = users.size() - withPhone;

        List<Map<String, Object>> registrationSources = new ArrayList<>();
        registrationSources.add(sourceItem("已绑定手机", withPhone));
        registrationSources.add(sourceItem("未绑定手机", withoutPhone));

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("userCounts", userCounts);
        result.put("registrationSources", registrationSources);
        result.put("totalUsers", users.size());
        result.put("totalHealthRecords", healthService.findAll().size());
        return result;
    }

    private Map<String, Object> sourceItem(String source, long count) {
        Map<String, Object> item = new HashMap<>();
        item.put("source", source);
        item.put("count", count);
        return item;
    }
}
