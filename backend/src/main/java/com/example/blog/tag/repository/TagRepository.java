package com.example.blog.tag.repository;

import com.example.blog.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

/**
 * 标签数据仓库
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    /** 获取所有标签，附带文章数 */
    @Query(value = "SELECT t.name, t.category, COUNT(at.article_id) as article_count " +
           "FROM tb_tag t LEFT JOIN tb_article_tag at ON t.id = at.tag_id " +
           "GROUP BY t.id ORDER BY article_count DESC", nativeQuery = true)
    List<Object[]> findAllWithCount();
}
