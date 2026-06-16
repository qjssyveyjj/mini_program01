package com.example.server.repository;

import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户 Repository 接口，继承 JpaRepository 提供 CRUD 操作
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 根据 openid 查询用户 */
    Optional<User> findByOpenid(String openid);

    /** 统计某时间段内新增用户数 */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
