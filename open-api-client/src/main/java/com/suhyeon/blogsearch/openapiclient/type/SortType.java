package com.suhyeon.blogsearch.openapiclient.type;

import lombok.Getter;

@Getter
public enum SortType {
    ACCURACY("accuracy", "sim"),
    RECENT("recency", "date"),
    DEFAULT("accuracy", "sim")
    ;

    private String kakao;
    private String naver;

    SortType(String kakao, String naver) {
        this.kakao = kakao;
        this.naver = naver;
    }
}
