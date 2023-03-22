package com.suhyeon.blogsearch.openapiclient.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Getter
public final class KakaoBlogSearchResponse extends OpenApiResponse {

    private Meta meta;
    private List<Document> documents;

    @Getter
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("pageable_count")
        private Integer pageableCount;

        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Getter
    public static class Document {
        @JsonProperty("blogname")
        private String blogName;
        private String title;
        private String contents;
        private String url;
        private String thumbnail;
        private String datetime;
    }

    public BlogSearchResponse toBlogSearchResponse() {
        return BlogSearchResponse.builder()
                                 .totalCount(this.meta.totalCount)
                                 .totalPage(this.meta.pageableCount)
                                 .contents(this.documents.stream()
                                                        .map(this::toContent)
                                                        .toList())
                                 .build();
    }

    private BlogSearchResponse.Content toContent(Document document) {
        return BlogSearchResponse.Content.builder()
                                         .blogName(document.getBlogName())
                                         .title(document.getTitle())
                                         .url(document.getUrl())
                                         .content(document.getContents())
                                         .createdAt(LocalDateTime.parse(document.getDatetime(), ISO_OFFSET_DATE_TIME))
                                         .thumbnail(document.getThumbnail())
                                         .build();
    }
}
