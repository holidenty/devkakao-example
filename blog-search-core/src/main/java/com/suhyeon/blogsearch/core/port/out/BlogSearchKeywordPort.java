package com.suhyeon.blogsearch.core.port.out;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

import com.suhyeon.blogsearch.entity.BlogSearchKeyword;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public interface BlogSearchKeywordPort {
    @Async
    void incrementKeywordCount(@NotBlank String keyword);
    List<BlogSearchKeyword> findPopularKeywords(@Min(1) @Max(10) Integer size);
}
