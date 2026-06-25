package com.example.blog.comment.service;

import com.example.blog.comment.entity.Comment;
import com.example.blog.comment.repository.CommentRepository;
import com.example.blog.common.PageResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论业务服务
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 递归构建评论树（支持无限嵌套）
     */
    public List<Map<String, Object>> getCommentsByArticle(Long articleId) {
        List<Comment> all = commentRepository.findByArticleIdOrderByCreateTimeAsc(articleId);
        // 获取所有根评论（parentId == null）
        List<Comment> roots = all.stream().filter(c -> c.getParentId() == null).toList();
        // 构建回复映射
        Map<Long, List<Comment>> replyMap = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment root : roots) {
            result.add(buildCommentTree(root, replyMap, 0));
        }
        return result;
    }

    /** 递归构建单个评论的树 */
    private Map<String, Object> buildCommentTree(Comment comment,
            Map<Long, List<Comment>> replyMap, int depth) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", comment.getId());
        map.put("articleId", comment.getArticleId());
        map.put("parentId", comment.getParentId());
        map.put("nickname", comment.getNickname());
        map.put("email", comment.getEmail());
        map.put("content", comment.getContent());
        map.put("createTime", comment.getCreateTime());
        map.put("depth", depth);  // 嵌套深度，前端用来缩进

        List<Comment> replies = replyMap.get(comment.getId());
        if (replies != null && !replies.isEmpty()) {
            List<Map<String, Object>> replyTrees = new ArrayList<>();
            for (Comment reply : replies) {
                replyTrees.add(buildCommentTree(reply, replyMap, depth + 1));
            }
            map.put("replies", replyTrees);
        } else {
            map.put("replies", Collections.emptyList());
        }
        return map;
    }

    /** 发表评论 */
    public Comment createComment(Long articleId, String nickname, String email,
                                  String content, Long parentId) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setNickname(nickname);
        comment.setEmail(email);
        comment.setContent(content);
        comment.setParentId(parentId);
        return commentRepository.save(comment);
    }

    /** 删除评论 */
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("评论不存在");
        }
        commentRepository.deleteById(id);
    }

    /** 分页查询所有评论（管理员用） */
    public PageResult<Map<String, Object>> getCommentsForAdmin(int page, int size) {
        PageRequest pr = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Comment> commentPage = commentRepository.findAllByOrderByCreateTimeDesc(pr);

        List<Map<String, Object>> records = commentPage.getContent().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("articleId", c.getArticleId());
            m.put("parentId", c.getParentId());
            m.put("nickname", c.getNickname());
            m.put("email", c.getEmail());
            m.put("content", c.getContent());
            m.put("createTime", c.getCreateTime());
            return m;
        }).collect(Collectors.toList());

        return new PageResult<>(records, commentPage.getTotalElements(), page, size);
    }
}
