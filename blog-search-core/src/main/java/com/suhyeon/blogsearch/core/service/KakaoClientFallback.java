package com.suhyeon.blogsearch.core.service;

import org.springframework.stereotype.Component;

import com.suhyeon.blogsearch.core.port.out.BlogSearchKeywordPort;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto;
import com.suhyeon.blogsearch.openapiclient.NaverClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClientFallback {

    private final NaverClient naverClient;
    private final BlogSearchKeywordPort blogSearchKeywordPort;

    @CircuitBreaker(name = "naverClient")
    public BlogSearchResultDto findBlogsFallback(BlogSearchDto dto) {
        log.warn("kakaoClient circuit breaker is open. naverClient call.");
        var blogSearchResponse = naverClient.blogSearchCache(dto.toBlogSearchRequest());

        return BlogSearchResultDto.builder()
                                  .totalCount(blogSearchResponse.totalCount())
                                  .totalPage(blogSearchResponse.totalPage())
                                  .contents(blogSearchResponse.contents().stream()
                                                             .map(BlogSearchResultDto::of)
                                                             .toList())
                                  .build();
    }
}
