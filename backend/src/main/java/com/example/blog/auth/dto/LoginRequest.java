package com.example.blog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码 token（从 captcha/generate 获取） */
    private String captchaToken;

    /** 用户输入的验证码 */
    private String captchaCode;
}
