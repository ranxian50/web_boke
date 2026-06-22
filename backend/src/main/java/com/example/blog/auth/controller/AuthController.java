package com.example.blog.auth.controller;

import com.example.blog.auth.dto.LoginRequest;
import com.example.blog.auth.dto.LoginResponse;
import com.example.blog.auth.dto.RegisterRequest;
import com.example.blog.auth.entity.User;
import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.auth.repository.UserRepository;
import com.example.blog.auth.service.UserService;
import com.example.blog.captcha.CaptchaService;
import com.example.blog.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    /**
     * 登录（需验证码验证）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 验证码校验
        if (!captchaService.verify(request.getCaptchaToken(), request.getCaptchaCode())) {
            return Result.badRequest("验证码错误或已过期，请刷新后重试");
        }

        // 认证
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        // 生成 Token
        String token = jwtUtil.generateToken(auth.getName());

        // 获取用户信息
        User user = userService.findByUsername(request.getUsername());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatar(), user.getRole());

        return Result.success(new LoginResponse(token, userInfo));
    }

    /**
     * 用户注册（需验证码验证）
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        // 验证码校验
        if (!captchaService.verify(request.getCaptchaToken(), request.getCaptchaCode())) {
            return Result.badRequest("验证码错误或已过期，请刷新后重试");
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            return Result.badRequest("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole("USER");
        userRepository.save(user);

        // 自动登录，返回token
        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), user.getRole());
        return Result.success(new LoginResponse(token, userInfo));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me() {
        // 从 SecurityContext 获取当前用户
        return Result.success(null); // 简化处理，后续完善
    }
}
