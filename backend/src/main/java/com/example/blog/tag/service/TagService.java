package com.example.blog.tag.service;

import com.example.blog.tag.entity.Tag;
import com.example.blog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签业务服务
 */
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /** 获取所有标签（含文章数量） */
    public List<Map<String, Object>> getAllTagsWithCount() {
        List<Object[]> results = tagRepository.findAllWithCount();
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("category", row[1]);
            map.put("articleCount", row[2]);
            return map;
        }).collect(Collectors.toList());
    }

    /** 获取所有标签名列表 */
    public List<String> getAllTagNames() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
