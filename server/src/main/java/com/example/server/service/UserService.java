package com.example.server.service;

import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户业务服务
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 根据 openid 获取用户，不存在则创建一个新用户 */
    public User findOrCreateByOpenid(String openid) {
        return userRepository.findByOpenid(openid).orElseGet(() -> {
            User user = new User();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            return userRepository.save(user);
        });
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
