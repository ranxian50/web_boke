package com.example.blog.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应体
 */
@Data
public class PageResult<T> {
    private List<T> records;   // 当前页数据
    private long total;        // 总记录数
    private int page;          // 当前页码
    private int size;          // 每页条数

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
