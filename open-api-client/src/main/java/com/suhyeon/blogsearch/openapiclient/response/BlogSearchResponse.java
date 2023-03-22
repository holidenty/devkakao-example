package com.suhyeon.blogsearch.openapiclient.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record BlogSearchResponse(
    Integer totalCount,
    Integer totalPage,
    List<Content> contents
) {

    @Builder
    public record Content(
        String blogName,
        String title,
        String url,
        String content,
        LocalDateTime createdAt,
        String thumbnail
    ) {
    }
}
