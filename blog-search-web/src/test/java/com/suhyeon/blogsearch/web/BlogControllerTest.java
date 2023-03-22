package com.suhyeon.blogsearch.web;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.suhyeon.blogsearch.core.port.in.BlogSearchUseCase;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchKeywordDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto;
import com.suhyeon.blogsearch.core.service.dto.BlogSearchResultDto.Content;
import com.suhyeon.blogsearch.web.config.GlobalExceptionHandler;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlogController.class)
@Import(GlobalExceptionHandler.class)
class BlogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogSearchUseCase blogSearchUseCase;

    @Test
    @DisplayName("블로그 검색 결과를 반환한다.")
    void findBlogs() throws Exception {
        // given
        var createdAt = LocalDateTime.now();
        var resultDto = BlogSearchResultDto.builder()
                                           .totalCount(1)
                                           .totalPage(1)
                                           .contents(List.of(Content.builder()
                                                                   .blogName("Test Blog")
                                                                   .title("Test Title")
                                                                   .url("http://example.com")
                                                                   .content("Test Content")
                                                                   .thumbnail("http://thumbnail.url")
                                                                   .createdAt(createdAt)
                                                                   .build()))
                                           .build();

        when(blogSearchUseCase.findBlogs(any()))
            .thenReturn(resultDto);

        // when
        mockMvc.perform(get("/blogs/search").queryParam("query", "test"))

        // then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.contents[0].blogName").value("Test Blog"))
               .andExpect(jsonPath("$.contents[0].title").value("Test Title"))
               .andExpect(jsonPath("$.contents[0].url").value("http://example.com"))
               .andExpect(jsonPath("$.contents[0].description").value("Test Content"))
               .andExpect(jsonPath("$.contents[0].thumbnail").value("http://thumbnail.url"))
               .andExpect(jsonPath("$.contents[0].createdAt").value(createdAt.format(ISO_DATE_TIME)))
               .andExpect(jsonPath("$.page").isNumber())
               .andExpect(jsonPath("$.size").isNumber())
               .andExpect(jsonPath("$.totalPage").isNumber())
               .andExpect(jsonPath("$.totalCount").isNumber());
    }

    @Test
    @DisplayName("검색 결과가 없을 경우 빈 리스트를 반환한다.")
    void findBlogs_noResults() throws Exception {
        // given
        var resultDto = BlogSearchResultDto.builder()
                                           .totalCount(0)
                                           .totalPage(0)
                                           .contents(Collections.emptyList())
                                           .build();

        when(blogSearchUseCase.findBlogs(any()))
            .thenReturn(resultDto);

        // when
        mockMvc.perform(get("/blogs/search").queryParam("query", "test"))

               // then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.contents", hasSize(0)))
               .andExpect(jsonPath("$.page").isNumber())
               .andExpect(jsonPath("$.size").isNumber())
               .andExpect(jsonPath("$.totalPage").isNumber())
               .andExpect(jsonPath("$.totalCount").isNumber());
    }

    @Test
    @DisplayName("검색어가 없을 경우 400 Bad Request를 반환한다.")
    void findBlogs_emptyQuery() throws Exception {
        // when
        mockMvc.perform(get("/blogs/search").queryParam("query", ""))

        // then
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.date").isString())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.status").value("Bad Request"))
               .andExpect(jsonPath("$.path").value("uri=/blogs/search"))
               .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("인기 검색어를 반환한다.")
    void popularKeywords() throws Exception {
        // given
        List<BlogSearchKeywordDto> mockBlogSearchKeywordDtos =
            IntStream.rangeClosed(1, 10)
                     .mapToObj(i -> new BlogSearchKeywordDto("keyword" + i, 11 - i)).toList();

        when(blogSearchUseCase.findPopularKeywords(any())).thenReturn(mockBlogSearchKeywordDtos);

        // when
        mockMvc.perform(get("/blogs/search/popular-keywords"))

        // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
               .andExpect(jsonPath("$[0].keyword").value("keyword1"))
               .andExpect(jsonPath("$[0].count").value(10))
               .andExpect(jsonPath("$[1].keyword").value("keyword2"))
               .andExpect(jsonPath("$[1].count").value(9))
               .andExpect(jsonPath("$[2].keyword").value("keyword3"))
               .andExpect(jsonPath("$[2].count").value(8))
               .andExpect(jsonPath("$[3].keyword").value("keyword4"))
               .andExpect(jsonPath("$[3].count").value(7))
               .andExpect(jsonPath("$[4].keyword").value("keyword5"))
               .andExpect(jsonPath("$[4].count").value(6))
               .andExpect(jsonPath("$[5].keyword").value("keyword6"))
               .andExpect(jsonPath("$[5].count").value(5))
               .andExpect(jsonPath("$[6].keyword").value("keyword7"))
               .andExpect(jsonPath("$[6].count").value(4))
               .andExpect(jsonPath("$[7].keyword").value("keyword8"))
               .andExpect(jsonPath("$[7].count").value(3))
               .andExpect(jsonPath("$[8].keyword").value("keyword9"))
               .andExpect(jsonPath("$[8].count").value(2))
               .andExpect(jsonPath("$[9].keyword").value("keyword10"))
               .andExpect(jsonPath("$[9].count").value(1));
    }

    @Test
    @DisplayName("인기 검색어가 없을 경우 빈 리스트를 반환한다.")
    void popularKeywords_noKeywords() throws Exception {
        // given
        when(blogSearchUseCase.findPopularKeywords(any())).thenReturn(Collections.emptyList());

        // when
        mockMvc.perform(get("/blogs/search/popular-keywords"))

               // then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(0)));
    }
}
