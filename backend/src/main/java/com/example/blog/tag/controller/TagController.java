package com.example.blog.tag.controller;

import com.example.blog.common.Result;
import com.example.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 标签接口控制器
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /** 获取所有标签（含文章数，用于首页展示） */
    @GetMapping
    public Result<List<Map<String, Object>>> getAllTags() {
        return Result.success(tagService.getAllTagsWithCount());
    }

    /** 获取所有标签名列表 */
    @GetMapping("/names")
    public Result<List<String>> getTagNames() {
        return Result.success(tagService.getAllTagNames());
    }
}
