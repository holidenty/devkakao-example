package com.suhyeon.blogsearch.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PRIVATE;

@OpenAPIDefinition(info = @Info(title = "Blog Search API", description = "Blog Search API Docs", version = "1.0"))
@Configuration(proxyBeanMethods = false)
@NoArgsConstructor(access = PRIVATE)
public class ApiDocsConfig {
    @Slf4j
    @Component
    public static class SwaggerUrlLogger implements ApplicationListener<ApplicationReadyEvent> {
        @Value("${server.port:8080}")
        private Integer serverPort;

        @Value("${springdoc.swagger-ui.path:/swagger-ui/index.html}")
        private String swaggerUiPath;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            log.info("Swagger UI: http://localhost:{}{}", serverPort, swaggerUiPath);
        }
    }
}
