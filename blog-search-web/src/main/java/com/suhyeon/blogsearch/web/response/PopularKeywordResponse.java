package com.suhyeon.blogsearch.web.response;

import com.suhyeon.blogsearch.core.service.dto.BlogSearchKeywordDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PopularKeywordResponse(
    @Schema(description = "인기 검색어")
    String keyword,
    @Schema(description = "검색 횟수")
    Integer count
) {
    public PopularKeywordResponse(BlogSearchKeywordDto dto) {
        this(dto.keyword(), dto.count());
    }
}
