package com.suhyeon.blogsearch.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suhyeon.blogsearch.entity.BlogSearchKeyword;

public interface BlogSearchKeywordRepository extends JpaRepository<BlogSearchKeyword, Long>, BlogSearchKeywordRepositoryCustom {
    Optional<BlogSearchKeyword> findByKeyword(String keyword);
}
