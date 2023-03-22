package com.suhyeon.blogsearch.openapiclient;

import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.response.KakaoBlogSearchResponse;
import com.suhyeon.blogsearch.openapiclient.response.OpenApiResponse;

public class KakaoClient extends AbstractOpenApiClient {
    public KakaoClient(WebClient kakaoWebClient) {
        super(kakaoWebClient);
    }

    @Override
    protected String getApiPath() {
        return "/v2/search/blog";
    }

    @Override
    protected Map<String, Object> getQueryParameters(BlogSearchRequest request) {
        return Map.of(
            "query", request.getQuery(),
            "sort", request.getSort().getKakao(),
            "page", request.getPage(),
            "size", request.getSize()
        );
    }

    @Override
    protected Class<? extends OpenApiResponse> getResponseClass() {
        return KakaoBlogSearchResponse.class;
    }
}
