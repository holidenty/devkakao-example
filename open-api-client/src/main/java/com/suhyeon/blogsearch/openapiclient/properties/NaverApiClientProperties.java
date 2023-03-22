package com.suhyeon.blogsearch.openapiclient.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "open-api.naver")
public class NaverApiClientProperties {
    private final String host;
    private final List<String> clientId;
    private final List<String> clientSecret;
}
