package com.suhyeon.blogsearch.web.response;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PageResponse<T>(
    @Schema(description = "페이지 번호")
    Integer page,
    @Schema(description = "페이지 사이즈")
    Integer size,
    @Schema(description = "전체 개수")
    Integer totalCount,
    @Schema(description = "전체 페이지 수")
    Integer totalPage,
    @Schema(description = "컨텐츠")
    List<T> contents
) {
}
