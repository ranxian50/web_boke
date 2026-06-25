package com.example.blog.auth.service;

import com.example.blog.auth.entity.User;
import com.example.blog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * 用户业务服务
 * 自动初始化管理员账号
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 应用启动时自动创建默认管理员账号 */
    @PostConstruct
    public void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 更新用户信息（昵称、头像）
     */
    public User updateProfile(Long userId, String nickname, String avatar) {
        User user = findById(userId);
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        return userRepository.save(user);
    }
}
