package com.example.server.controller;

import com.example.server.entity.User;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 用户身份验证控制器：处理微信登录请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 微信登录接口：前端传入 code，调用微信 jscode2session 获取 openid。
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少登录 code"));
        }

        String wxApiUrl = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + appId
                + "&secret=" + appSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        @SuppressWarnings("unchecked")
        Map<String, Object> result = restTemplate.getForObject(wxApiUrl, Map.class);

        if (result == null || result.get("openid") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "登录失败", "detail", result == null ? "无响应" : result));
        }

        String openid = (String) result.get("openid");
        User user = userService.findOrCreateByOpenid(openid);
        return ResponseEntity.ok(user);
    }
}
