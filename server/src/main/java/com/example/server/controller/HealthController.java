package com.example.server.controller;

import com.example.server.entity.HealthData;
import com.example.server.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 健康数据控制器
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /** 录入健康数据 */
    @PostMapping
    public ResponseEntity<HealthData> create(@RequestBody HealthData data) {
        return ResponseEntity.ok(healthService.save(data));
    }

    /** 查询某用户的全部健康数据 */
    @GetMapping
    public ResponseEntity<List<HealthData>> listByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(healthService.listByUser(userId));
    }

    /** 查询某用户最新一条健康数据 */
    @GetMapping("/latest")
    public ResponseEntity<?> latest(@RequestParam Long userId) {
        return healthService.latestByUser(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** 后台：查询全部健康数据 */
    @GetMapping("/all")
    public ResponseEntity<List<HealthData>> all() {
        return ResponseEntity.ok(healthService.findAll());
    }

    /** 删除一条健康数据 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
