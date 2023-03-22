package com.suhyeon.blogsearch.openapiclient.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.suhyeon.blogsearch.openapiclient.KakaoClient;
import com.suhyeon.blogsearch.openapiclient.NaverClient;
import com.suhyeon.blogsearch.openapiclient.properties.KakaoApiClientProperties;
import com.suhyeon.blogsearch.openapiclient.properties.NaverApiClientProperties;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties({KakaoApiClientProperties.class, NaverApiClientProperties.class})
public class OpenApiAutoConfiguration {

    private final KakaoApiClientProperties kakaoApiClientProperties;
    private final NaverApiClientProperties naverApiClientProperties;

    private static final String KAKAO_CLIENT_KEY_PREFIX = "KakaoAK ";
    private static final String NAVER_CLIENT_ID = "X-Naver-Client-Id";
    private static final String NAVER_CLIENT_SECRET = "X-Naver-Client-Secret";

    @Bean
    @ConditionalOnMissingBean(name = "kakaoWebClient")
    public WebClient kakaoWebClient() {
        var kakaoApiKey = String.join("", kakaoApiClientProperties.getRestApiKey());

        return this.getDefaultWebClientBuilder()
                   .baseUrl(kakaoApiClientProperties.getHost())
                   .defaultHeader(AUTHORIZATION, KAKAO_CLIENT_KEY_PREFIX + kakaoApiKey)
                   .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "naverWebClient")
    public WebClient naverWebClient() {
        return this.getDefaultWebClientBuilder()
                   .baseUrl(naverApiClientProperties.getHost())
                   .defaultHeaders(httpHeaders -> {
                          httpHeaders.set(NAVER_CLIENT_ID, String.join("", naverApiClientProperties.getClientId()));
                          httpHeaders.set(NAVER_CLIENT_SECRET, String.join("", naverApiClientProperties.getClientSecret()));
                   })
                   .build();
    }

    @Bean
    public KakaoClient kakaoClient(WebClient kakaoWebClient) {
        return new KakaoClient(kakaoWebClient);
    }

    @Bean
    public NaverClient naverClient(WebClient naverWebClient) {
        return new NaverClient(naverWebClient);
    }

    private HttpClient getDefaultHttpClient() {
        return HttpClient.create()
                         .option(CONNECT_TIMEOUT_MILLIS, 5000)
                         .responseTimeout(Duration.ofMillis(5000))
                            .doOnConnected(conn -> {
                                conn.addHandlerLast(new ReadTimeoutHandler(5000, MILLISECONDS));
                                conn.addHandlerLast(new WriteTimeoutHandler(5000, MILLISECONDS));
                            });
    }

    private WebClient.Builder getDefaultWebClientBuilder() {
        var exchangeStrategies = ExchangeStrategies.builder()
                          .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                          .build();

        return WebClient.builder()
                        .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                        .exchangeStrategies(exchangeStrategies)
                        .clientConnector(new ReactorClientHttpConnector(this.getDefaultHttpClient()));
    }
}
