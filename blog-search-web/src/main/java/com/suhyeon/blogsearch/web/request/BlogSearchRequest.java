package com.suhyeon.blogsearch.web.request;

import java.util.Optional;

import com.suhyeon.blogsearch.core.service.dto.BlogSearchDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
public class BlogSearchRequest {
    @NotBlank
    @Schema(description = "검색어", example = "심수현", requiredMode = REQUIRED)
    private String query;

    @Min(1)
    @Max(50)
    @Schema(description = "페이지 번호(기본값: 1)", example = "1", minimum = "1", maximum = "50")
    private Integer page;

    @Min(1)
    @Max(50)
    @Schema(description = "페이지 사이즈(기본값: 10)", example = "10", minimum = "1", maximum = "50")
    private Integer size;

    @Schema(description = "정렬 방식(기본값: 정확도)", example = "ACCURACY", allowableValues = {"ACCURACY(정확도)", "RECENT(최신순)"})
    private BlogSearchSortType sort;

    public BlogSearchRequest(String query, Integer page, Integer size, BlogSearchSortType sort) {
        this.query = query;
        this.page = Optional.ofNullable(page).orElse(1);
        this.size = Optional.ofNullable(size).orElse(10);
        this.sort = sort;
    }

    public BlogSearchDto toBlogSearchDto() {
        return BlogSearchDto.builder()
                            .query(query)
                            .page(page)
                            .size(size)
                            .sort(sort == null ? null : sort.toCoreBlogSearchSortType())
                            .build();
    }

    public enum BlogSearchSortType {
        ACCURACY, RECENT
        ;

        public BlogSearchDto.BlogSearchSortType toCoreBlogSearchSortType() {
            switch (this) {
                case ACCURACY:
                    return BlogSearchDto.BlogSearchSortType.ACCURACY;
                case RECENT:
                    return BlogSearchDto.BlogSearchSortType.RECENT;
                default:
                    return null;
            }
        }
    }

}
