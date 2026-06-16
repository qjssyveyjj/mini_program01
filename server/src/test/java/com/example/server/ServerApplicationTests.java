package com.example.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 上下文加载测试依赖 MySQL 与 Spring AI 配置，默认禁用以免本地构建失败。
// 配置好数据库与 OPENAI_API_KEY 后可移除 @Disabled 运行。
@Disabled("需要 MySQL 与 Spring AI 配置后再启用")
@SpringBootTest
class ServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
