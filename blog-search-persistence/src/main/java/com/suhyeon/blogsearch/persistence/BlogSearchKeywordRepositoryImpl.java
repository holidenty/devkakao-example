package com.suhyeon.blogsearch.persistence;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.suhyeon.blogsearch.entity.BlogSearchKeyword;

import lombok.RequiredArgsConstructor;

import static com.suhyeon.blogsearch.entity.QBlogSearchKeyword.blogSearchKeyword;

@RequiredArgsConstructor
public class BlogSearchKeywordRepositoryImpl implements BlogSearchKeywordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BlogSearchKeyword> findTopCount(Integer size) {
        return jpaQueryFactory.selectFrom(blogSearchKeyword)
                              .orderBy(blogSearchKeyword.count.desc(), blogSearchKeyword.updatedAt.desc())
                              .limit(size)
                              .fetch();
    }
}
