package com.suhyeon.blogsearch.openapiclient.response;

public sealed abstract class OpenApiResponse permits KakaoBlogSearchResponse, NaverBlogSearchResponse {
    public abstract BlogSearchResponse toBlogSearchResponse();
}
