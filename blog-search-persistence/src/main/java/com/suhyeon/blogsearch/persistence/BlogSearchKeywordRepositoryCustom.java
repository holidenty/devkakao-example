package com.suhyeon.blogsearch.persistence;

import java.util.List;

import com.suhyeon.blogsearch.entity.BlogSearchKeyword;

public interface BlogSearchKeywordRepositoryCustom {
    List<BlogSearchKeyword> findTopCount(Integer size);
}
