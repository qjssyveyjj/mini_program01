package com.example.server.controller;

import com.example.server.service.AiChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 智能客服控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 同步问答：返回 {"answer": "..."}
     */
    @GetMapping("/chat")
    public Map<String, String> chat(@RequestParam String message) {
        try {
            return Map.of("answer", aiChatService.ask(message));
        } catch (Exception e) {
            return Map.of("answer", "抱歉，智能客服暂时不可用，请稍后再试。");
        }
    }

    /**
     * 流式问答：通过 SSE 返回回答片段
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return aiChatService.stream(message)
                .onErrorReturn("抱歉，智能客服暂时不可用，请稍后再试。");
    }
}
