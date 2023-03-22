package com.suhyeon.blogsearch.core.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.suhyeon.blogsearch.core.port.in.BlogSearchUseCase;
import com.suhyeon.blogsearch.core.port.out.BlogSearchKeywordPort;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchKeywordDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto;
import com.suhyeon.blogsearch.openapiclient.OpenApiClient;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BlogService implements BlogSearchUseCase {

    private final OpenApiClient kakaoClient;
    private final BlogSearchKeywordPort blogSearchKeywordPort;
    private final KakaoClientFallback kakaoClientFallback;

    /**
     * WebClientRequestException 발생 시 retry 3회 수행하고
     * 그 후 CircuitBreaker가 열리면 fallback 메서드 호출
     */
    @Override
    @CircuitBreaker(name = "kakaoClient", fallbackMethod = "findBlogsCBFallback")
    @Retry(name = "kakaoClient", fallbackMethod = "findBlogsRetryFallback")
    public BlogSearchResultDto findBlogs(@Valid BlogSearchDto dto) {
        var blogSearchResponse = kakaoClient.blogSearchCache(dto.toBlogSearchRequest());

        return BlogSearchResultDto.builder()
                                  .totalCount(blogSearchResponse.totalCount())
                                  .totalPage(blogSearchResponse.totalPage())
                                  .contents(blogSearchResponse.contents().stream()
                                                             .map(BlogSearchResultDto::of)
                                                             .toList())
                                  .build();
    }

    public void incrementKeywordCount(@NotBlank String keyword) {
        blogSearchKeywordPort.incrementKeywordCount(keyword);
    }

    @Override
    @Cacheable(value = "popularKeyword", key = "#size")
    public List<BlogSearchKeywordDto> findPopularKeywords(@Min(1) @Max(10) Integer size) {
        return blogSearchKeywordPort.findPopularKeywords(size).stream()
                                    .map(blogSearchKeyword ->
                                             new BlogSearchKeywordDto(blogSearchKeyword.getKeyword(), blogSearchKeyword.getCount()))
                                    .toList();
    }

    public BlogSearchResultDto findBlogsRetryFallback(BlogSearchDto dto, WebClientRequestException e) {
        return kakaoClientFallback.findBlogsFallback(dto);
    }

    public BlogSearchResultDto findBlogsCBFallback(BlogSearchDto dto, CallNotPermittedException e) {
        return kakaoClientFallback.findBlogsFallback(dto);
    }
}
