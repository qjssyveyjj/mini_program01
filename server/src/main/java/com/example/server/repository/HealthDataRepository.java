package com.example.server.repository;

import com.example.server.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 健康数据 Repository 接口
 */
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

    /** 按用户查询健康记录（时间倒序） */
    List<HealthData> findByUserIdOrderByTimestampDesc(Long userId);

    /** 查询用户最新一条健康记录 */
    Optional<HealthData> findFirstByUserIdOrderByTimestampDesc(Long userId);
}
