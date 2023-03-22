package com.suhyeon.blogsearch.core.port.in;

import java.util.List;

import com.suhyeon.blogsearch.core.service.dto.BlogSearchDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchKeywordDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public interface BlogSearchUseCase {
    BlogSearchResultDto findBlogs(@Valid BlogSearchDto dto);
    void incrementKeywordCount(@NotBlank String keyword);
    List<BlogSearchKeywordDto> findPopularKeywords(@Min(1) @Max(10) Integer size);
}
