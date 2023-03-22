package com.suhyeon.blogsearch.openapiclient;

import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.response.NaverBlogSearchResponse;
import com.suhyeon.blogsearch.openapiclient.response.OpenApiResponse;

public class NaverClient extends AbstractOpenApiClient {

    public NaverClient(WebClient naverWebClient) {
        super(naverWebClient);
    }

    @Override
    protected String getApiPath() {
        return "/v1/search/blog";
    }

    @Override
    protected Map<String, Object> getQueryParameters(BlogSearchRequest request) {
        return Map.of(
            "query", request.getQuery(),
            "sort", request.getSort().getNaver(),
            "display", request.getSize(),
            "start", request.getPage()
        );
    }

    @Override
    protected Class<? extends OpenApiResponse> getResponseClass() {
        return NaverBlogSearchResponse.class;
    }
}
