package com.example.blog.auth.dto;

import lombok.Data;

/**
 * 用户信息更新请求 DTO
 */
@Data
public class UserUpdateRequest {
    private String nickname;
    private String avatar;
}
