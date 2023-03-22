package com.suhyeon.blogsearch.core.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.suhyeon.blogsearch.openapiclient.response.BlogSearchResponse;

import lombok.Builder;

@Builder
public record BlogSearchResultDto(
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

    public static BlogSearchResultDto.Content of(BlogSearchResponse.Content content) {
        return BlogSearchResultDto.Content.builder()
                                  .blogName(content.blogName())
                                  .title(content.title())
                                  .url(content.url())
                                  .content(content.content())
                                  .createdAt(content.createdAt())
                                  .thumbnail(content.thumbnail())
                                  .build();
    }
}
