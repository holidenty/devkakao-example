package com.suhyeon.blogsearch.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(
    indexes = {
        @Index(name = "idx_blog_search_keyword_keyword", columnList = "keyword"),
        @Index(name = "idx_blog_search_keyword_count_updated_at", columnList = "count desc, updatedAt desc")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_blog_search_keyword_keyword", columnNames = "keyword")
    }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class BlogSearchKeyword {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 100)
    private String keyword;

    private Integer count;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public BlogSearchKeyword(String keyword) {
        this.keyword = keyword;
        this.count = 1;
    }

    public void incrementCount() {
        this.count++;
    }
}
