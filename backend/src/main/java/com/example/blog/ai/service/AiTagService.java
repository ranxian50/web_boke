package com.example.blog.ai.service;

import com.example.blog.ai.config.AiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 标签服务
 * 调用 DeepSeek v4 Flash API 根据文章摘要自动生成标签
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiTagService {

    private final AiConfig aiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 根据文章前80字生成标签
     * @param summary 文章摘要（前80字）
     * @return 标签列表（1~5个）
     */
    public List<String> generateTags(String summary) {
        try {
            // 构建请求体
            String requestBody = objectMapper.writeValueAsString(
                Map.of(
                    "model", "deepseek-chat",
                    "messages", List.of(
                        Map.of("role", "system", "content",
                            "你是一个技术博客标签分类器。根据文章开头内容，判断这篇文章" +
                            "涉及哪些编程语言、框架、工具或技术领域。" +
                            "只返回 JSON 格式：{\"tags\": [\"标签1\", \"标签2\", ...]}" +
                            "每个标签不超过20个字符，返回1~5个标签。"),
                        Map.of("role", "user", "content",
                            "根据以下文章开头判断技术标签：\n" + summary)
                    ),
                    "temperature", 0.3,
                    "max_tokens", 200
                )
            );

            // 发送 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiConfig.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.at("/choices/0/message/content").asText();

            // 提取 JSON 中的 tags 数组
            JsonNode tagsNode = objectMapper.readTree(content).get("tags");
            if (tagsNode != null && tagsNode.isArray()) {
                List<String> tags = new ArrayList<>();
                tagsNode.forEach(t -> tags.add(t.asText()));
                return tags;
            }
        } catch (Exception e) {
            log.error("AI 标签生成失败", e);
        }

        // 失败时返回空列表（不影响文章发布）
        return List.of();
    }
}
