package com.suhyeon.blogsearch.core.service.dto;

import java.util.Optional;

import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.type.SortType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import static com.suhyeon.blogsearch.openapiclient.type.SortType.DEFAULT;

@Getter
public class BlogSearchDto {
    @NotBlank
    private String query;

    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    private BlogSearchSortType sort;

    @Builder
    public BlogSearchDto(String query, Integer page, Integer size, BlogSearchSortType sort) {
        this.query = query;
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public BlogSearchRequest toBlogSearchRequest() {
        return BlogSearchRequest.builder()
                                .query(query)
                                .page(page)
                                .size(size)
                                .sort(Optional.ofNullable(sort).map(BlogSearchSortType::toSortType).orElse(DEFAULT))
                                .build();
    }

    public enum BlogSearchSortType {
        ACCURACY, RECENT
        ;

        public SortType toSortType() {
            switch (this) {
                case ACCURACY:
                    return SortType.ACCURACY;
                case RECENT:
                    return SortType.RECENT;
                default:
                    return SortType.DEFAULT;
            }
        }
    }
}
