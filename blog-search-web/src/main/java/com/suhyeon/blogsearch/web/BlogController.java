package com.suhyeon.blogsearch.web;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suhyeon.blogsearch.core.port.in.BlogSearchUseCase;
import com.suhyeon.blogsearch.web.request.BlogSearchRequest;
import com.suhyeon.blogsearch.web.response.BlogResponse;
import com.suhyeon.blogsearch.web.response.PageResponse;
import com.suhyeon.blogsearch.web.response.PopularKeywordResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "블로그 검색")
@Validated
@RestController
@RequestMapping("/blogs/search")
@RequiredArgsConstructor
public class BlogController {

    private final BlogSearchUseCase blogSearchUseCase;

    @Operation(summary = "블로그 검색", description = "블로그 검색을 수행합니다.")
    @GetMapping
    public PageResponse<BlogResponse> findBlogs(
        @ParameterObject @Valid BlogSearchRequest request
    ) {
        blogSearchUseCase.incrementKeywordCount(request.getQuery());
        var blogSearchResponse = blogSearchUseCase.findBlogs(request.toBlogSearchDto());

        return PageResponse.<BlogResponse>builder()
                           .page(request.getPage())
                           .size(request.getSize())
                           .totalPage(blogSearchResponse.totalPage())
                           .totalCount(blogSearchResponse.totalCount())
                           .contents(blogSearchResponse.contents().stream()
                                                      .map(BlogResponse::of)
                                                      .toList())
                           .build();
    }

    @Operation(summary = "인기 검색어 조회", description = "인기 검색어를 조회합니다. 정렬: 검색 횟수 내림차순, 갱신일 내림차순")
    @GetMapping("/popular-keywords")
    public List<PopularKeywordResponse> popularKeywords(
        @RequestParam(defaultValue = "10") @Min(1) @Max(10) Integer size
    ) {
        return blogSearchUseCase.findPopularKeywords(size).stream()
                                .map(PopularKeywordResponse::new)
                                .toList();
    }
}
