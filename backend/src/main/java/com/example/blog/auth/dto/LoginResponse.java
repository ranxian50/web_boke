package com.example.blog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 DTO（返回 Token + 用户信息）
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String role;
    }
}
