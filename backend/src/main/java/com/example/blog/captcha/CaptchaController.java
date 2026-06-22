package com.example.blog.captcha;

import com.example.blog.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 验证码接口控制器
 */
@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /** 生成验证码（返回 token + base64 图片） */
    @GetMapping("/generate")
    public Result<Map<String, Object>> generate() {
        return Result.success(captchaService.generate());
    }

    /** 验证验证码（内部调用，也可对外暴露） */
    @PostMapping("/verify")
    public Result<Boolean> verify(@RequestParam String token, @RequestParam String code) {
        return Result.success(captchaService.verify(token, code));
    }
}
