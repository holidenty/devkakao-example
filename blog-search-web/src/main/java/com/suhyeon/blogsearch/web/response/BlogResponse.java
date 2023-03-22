package com.suhyeon.blogsearch.web.response;

import java.time.LocalDateTime;

import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto.Content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BlogResponse(
    @Schema(description = "블로그 이름")
    String blogName,
    @Schema(description = "제목")
    String title,
    @Schema(description = "내용")
    String description,
    @Schema(description = "URL")
    String url,
    @Schema(description = "썸네일")
    String thumbnail,
    @Schema(description = "생성일시")
    LocalDateTime createdAt
) {
    public static BlogResponse of(Content content) {
        return BlogResponse.builder()
                           .blogName(content.blogName())
                           .title(content.title())
                           .description(content.content())
                           .url(content.url())
                           .thumbnail(content.thumbnail())
                           .createdAt(content.createdAt())
                           .build();
    }
}
