package com.suhyeon.blogsearch.openapiclient;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.suhyeon.blogsearch.openapiclient.request.BlogSearchRequest;
import com.suhyeon.blogsearch.openapiclient.response.BlogSearchResponse;
import com.suhyeon.blogsearch.openapiclient.response.OpenApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class AbstractOpenApiClient implements OpenApiClient {
    protected final WebClient webClient;

    protected abstract String getApiPath();
    protected abstract Map<String, Object> getQueryParameters(BlogSearchRequest request);
    protected abstract Class<? extends OpenApiResponse> getResponseClass();

    @Override
    public Mono<BlogSearchResponse> blogSearch(@Valid BlogSearchRequest request) {
        var uriComponentBuilder = UriComponentsBuilder.fromPath(this.getApiPath());
        getQueryParameters(request).forEach(uriComponentBuilder::queryParam);

        return webClient.get()
                        .uri(uriComponentBuilder.build().toUriString())
                        .retrieve()
                        .bodyToMono(this.getResponseClass())
                        .map(OpenApiResponse::toBlogSearchResponse);
    }

    @Cacheable(value = "blogSearch", key = "#request.toString()")
    public BlogSearchResponse blogSearchCache(BlogSearchRequest request) {
        return this.blogSearch(request).block();
    }
}
