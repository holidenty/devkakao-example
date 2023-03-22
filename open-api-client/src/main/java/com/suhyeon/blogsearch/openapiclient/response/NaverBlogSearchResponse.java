package com.suhyeon.blogsearch.openapiclient.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

@Getter
public final class NaverBlogSearchResponse extends OpenApiResponse {
    private String lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<Item> items;

    @Getter
    public static class Item {
        private String title;
        private String link;
        private String description;
        private String bloggername;
        private String bloggerlink;
        private String postdate;
    }

    public BlogSearchResponse toBlogSearchResponse() {
        return BlogSearchResponse.builder()
                                 .totalCount(this.total)
                                 .totalPage((int) Math.ceil((double) this.total / this.display))
                                 .contents(this.items.stream()
                                                    .map(this::toContent)
                                                    .toList())
                                 .build();
    }

    private BlogSearchResponse.Content toContent(Item item) {
        return BlogSearchResponse.Content.builder()
                                         .blogName(item.getBloggername())
                                         .title(item.getTitle())
                                         .url(item.getLink())
                                         .content(item.getDescription())
                                         .createdAt(LocalDate.parse(item.getPostdate(), BASIC_ISO_DATE).atStartOfDay())
                                         .build();
    }
}
