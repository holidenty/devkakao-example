package com.suhyeon.blogsearch.openapiclient.request;

import java.util.Optional;

import com.suhyeon.blogsearch.openapiclient.type.SortType;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import static com.suhyeon.blogsearch.openapiclient.type.SortType.DEFAULT;

@Getter
@ToString
public class BlogSearchRequest {
    @NotBlank
    private String query;

    private Integer page;

    private Integer size;

    private SortType sort;

    @Builder
    public BlogSearchRequest(String query, Integer page, Integer size, SortType sort) {
        this.query = query;
        this.page = Optional.ofNullable(page).orElse(1);
        this.size = Optional.ofNullable(size).orElse(10);
        this.sort = Optional.ofNullable(sort).orElse(DEFAULT);
    }
}
