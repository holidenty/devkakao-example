package com.suhyeon.blogsearch.openapiclient;

import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.response.BlogSearchResponse;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface OpenApiClient {
    Mono<BlogSearchResponse> blogSearch(@Valid BlogSearchRequest request);
    BlogSearchResponse blogSearchCache(@Valid BlogSearchRequest request);
}
