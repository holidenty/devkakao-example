package com.suhyeon.blogsearch.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.suhyeon.blogsearch.entity.BlogSearchKeyword;
import com.suhyeon.blogsearch.persistence.BlogSearchKeywordRepository;
import com.suhyeon.blogsearch.persistence.BlogSearchPersistenceAdapter;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogSearchPersistenceAdapterTest {

    @InjectMocks
    private BlogSearchPersistenceAdapter blogSearchPersistenceAdapter;

    @Mock
    private BlogSearchKeywordRepository blogSearchKeywordRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
        ReflectionTestUtils.setField(blogSearchPersistenceAdapter, "hazelcastInstance", Hazelcast.newHazelcastInstance(config));
    }

    @Test
    @DisplayName("n개의 스레드로 m번씩 키워드 카운트 증가 테스트(hazelcast 분산락 테스트)")
    void testIncrementKeywordCount() throws InterruptedException {
        // given
        var keyword = "test";
        var blogSearchKeyword = new BlogSearchKeyword(keyword);
        when(blogSearchKeywordRepository.findByKeyword(keyword))
            .thenReturn(Optional.of(blogSearchKeyword));
        var threadSize = 100;
        var iterationSize = 100;

        // when
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadSize; i++) {
            var thread = new Thread(() -> {
                for (int j = 0; j < iterationSize; j++) {
                    blogSearchPersistenceAdapter.incrementKeywordCount(keyword);
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // then
        assertEquals(threadSize * iterationSize + 1, blogSearchKeyword.getCount());
    }

    @Test
    void findPopularKeywords() {
        // given
        var size = 10;
        var mockBlogSearchKeywords = List.of(
            new BlogSearchKeyword("test1"),
            new BlogSearchKeyword("test2"),
            new BlogSearchKeyword("test3")
        );

        when(blogSearchKeywordRepository.findTopCount(eq(size)))
            .thenReturn(mockBlogSearchKeywords);

        // when
        var blogSearchKeywords = blogSearchPersistenceAdapter.findPopularKeywords(size);

        // then
        assertThat(blogSearchKeywords).hasSize(3);
        assertThat(blogSearchKeywords.get(0).getKeyword()).isEqualTo("test1");
        assertThat(blogSearchKeywords.get(1).getKeyword()).isEqualTo("test2");
        assertThat(blogSearchKeywords.get(2).getKeyword()).isEqualTo("test3");
        verify(blogSearchKeywordRepository, times(1)).findTopCount(eq(size));
    }
}
