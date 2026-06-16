package com.example.server.service;

import com.example.server.entity.HealthData;
import com.example.server.repository.HealthDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 健康数据业务服务
 */
@Service
public class HealthService {

    private final HealthDataRepository healthDataRepository;

    public HealthService(HealthDataRepository healthDataRepository) {
        this.healthDataRepository = healthDataRepository;
    }

    /** 保存一条健康数据；未指定时间时默认当前时间 */
    public HealthData save(HealthData data) {
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        return healthDataRepository.save(data);
    }

    public List<HealthData> listByUser(Long userId) {
        return healthDataRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public Optional<HealthData> latestByUser(Long userId) {
        return healthDataRepository.findFirstByUserIdOrderByTimestampDesc(userId);
    }

    public List<HealthData> findAll() {
        return healthDataRepository.findAll();
    }

    public void deleteById(Long id) {
        healthDataRepository.deleteById(id);
    }
}
