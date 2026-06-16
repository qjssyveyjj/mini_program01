package com.example.server.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 智能客服服务：使用 Spring AI（OpenAI 兼容接口）调用阿里百炼大模型进行问答。
 *
 * 说明：本实现采用 OpenAI 兼容方式对接 DashScope，未引入 Spring AI Alibaba
 * 专有的知识库检索器（DashScopeDocumentRetriever）。如需完整 RAG，可在此扩展。
 */
@Service
public class AiChatService {

    /** 系统提示词：限定为健康顾问角色 */
    private static final String SYSTEM_PROMPT =
            "你是熊猫哥健康管理系统的健康顾问助手。请用简洁、专业、友好的中文回答用户的健康相关问题，" +
            "涉及饮食、运动、作息、血压、心率、血氧、体重管理等。当问题超出健康范畴或需要就医时，请提醒用户咨询专业医生。";

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    /**
     * 同步问答：返回完整回答文本
     */
    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 流式问答：以 Flux 形式返回回答内容片段（用于 SSE）
     */
    public Flux<String> stream(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
