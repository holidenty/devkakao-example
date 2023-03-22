package com.suhyeon.blogsearch.persistence;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import com.hazelcast.core.HazelcastInstance;
import com.suhyeon.blogsearch.core.exception.BlogSearchException;
import com.suhyeon.blogsearch.core.port.out.BlogSearchKeywordPort;
import com.suhyeon.blogsearch.entity.BlogSearchKeyword;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.suhyeon.blogsearch.core.exception.BlogSearchExceptionCode.LOCK_TIMEOUT;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Repository
@Validated
@RequiredArgsConstructor
public class BlogSearchPersistenceAdapter implements BlogSearchKeywordPort {
    private final BlogSearchKeywordRepository blogSearchKeywordRepository;
    private final HazelcastInstance hazelcastInstance;

    @Async
    @Override
    public void incrementKeywordCount(@NotBlank String keyword) {
        var lock = hazelcastInstance.getCPSubsystem().getLock("incrementKeywordCount");

        if(lock.tryLock(10, SECONDS)) {
            try {
                this.incrementAndSaveKeywordCount(keyword);
            } finally {
                lock.unlock();
            }
        } else {
            throw new BlogSearchException(LOCK_TIMEOUT);
        }
    }

    private void incrementAndSaveKeywordCount(String keyword) {
        var blogSearchKeyword = blogSearchKeywordRepository.findByKeyword(keyword)
                                                           .map(this::incrementCount)
                                                           .orElseGet(() -> new BlogSearchKeyword(keyword));

        blogSearchKeywordRepository.save(blogSearchKeyword);
    }

    private BlogSearchKeyword incrementCount(BlogSearchKeyword blogSearchKeyword) {
        blogSearchKeyword.incrementCount();
        return blogSearchKeyword;
    }

    @Override
    public List<BlogSearchKeyword> findPopularKeywords(@Min(1) @Max(10) Integer size) {
        return blogSearchKeywordRepository.findTopCount(size);
    }
}
