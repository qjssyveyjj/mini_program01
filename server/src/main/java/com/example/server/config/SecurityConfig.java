package com.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置：开发阶段放行接口与静态资源。
 *
 * 生产环境应对 /api/users、/api/health/all、/api/stats 等后台接口
 * 加入管理员认证（如 JWT 或表单登录）。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**", "/uploads/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
