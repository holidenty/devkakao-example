package com.suhyeon.blogsearch.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.suhyeon.blogsearch.core.port.out.BlogSearchKeywordPort;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchKeywordDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto;
import com.suhyeon.blogsearch.entity.BlogSearchKeyword;
import com.suhyeon.blogsearch.openapiclient.OpenApiClient;
import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.response.BlogSearchResponse;
import com.suhyeon.blogsearch.openapiclient.response.BlogSearchResponse.Content;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @InjectMocks
    private BlogService blogService;

    @Mock
    private OpenApiClient kakaoClient;

    @Mock
    private BlogSearchKeywordPort blogSearchKeywordPort;

    @Mock
    private KakaoClientFallback kakaoClientFallback;

    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setup() {
        circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("blogSearch");
        circuitBreaker.transitionToClosedState();
        circuitBreakerRegistry.circuitBreaker("blogSearch").transitionToClosedState();
    }

    @Test
    @DisplayName("블로그 검색 테스트")
    void testFindBlogs() {
        // given
        var dto = BlogSearchDto.builder()
                               .query("test")
                               .page(1)
                               .size(10)
                               .build();
        var blogSearchResponse = BlogSearchResponse.builder()
                                                   .totalCount(2)
                                                   .totalPage(1)
                                                   .contents(this.mockContents())
                                                   .build();

        when(kakaoClient.blogSearchCache(any(BlogSearchRequest.class))).thenReturn(blogSearchResponse);

        // when
        BlogSearchResultDto result = blogService.findBlogs(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.totalPage()).isEqualTo(1);
        assertThat(result.contents()).isNotNull();
        verify(kakaoClient, Mockito.times(1)).blogSearchCache(any(BlogSearchRequest.class));
    }

    @Test
    @DisplayName("블로그 검색 키워드 카운팅")
    void testIncrementKeywordCount() {
        // given
        var keyword = "test";
        doNothing().when(blogSearchKeywordPort).incrementKeywordCount(keyword);

        // when
        blogService.incrementKeywordCount(keyword);

        // then
        verify(blogSearchKeywordPort, Mockito.times(1)).incrementKeywordCount(keyword);
    }

    @Test
    @DisplayName("인기 검색어 테스트")
    void testFindPopularKeywords() {
        // given
        var size = 10;
        var popularKeywords = List.of(
            new BlogSearchKeyword("keyword1"),
            new BlogSearchKeyword("keyword2"),
            new BlogSearchKeyword("keyword3")
        );
        when(blogSearchKeywordPort.findPopularKeywords(size)).thenReturn(popularKeywords);

        // when
        List<BlogSearchKeywordDto> result = blogService.findPopularKeywords(size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).keyword()).isEqualTo("keyword1");
        assertThat(result.get(0).count()).isEqualTo(1);
    }

    private List<BlogSearchResponse.Content> mockContents() {
        var createdAt = LocalDateTime.now();

        return List.of(
            Content.builder()
                   .blogName("Blog1")
                   .title("Title1")
                   .url("https://example.com/1")
                   .content("Content1")
                   .createdAt(createdAt)
                   .thumbnail("https://example.com/thumbnail1")
                   .build(),
            Content.builder()
                   .blogName("Blog2")
                   .title("Title2")
                   .url("https://example.com/2")
                   .content("Content2")
                   .createdAt(createdAt)
                   .thumbnail("https://example.com/thumbnail2")
                   .build()
        );
    }
}
