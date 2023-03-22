# 블로그 검색 API

### 개발 환경
- Java 17
- Spring Boot 3.0.4
- Gradle 7.6.1
- H2 Database
- JUnit 5

### Jar 파일 링크
https://github.com/bitosh/my-assignment/releases/download/release-0.0.1-SNAPSHOT/blog-search-application-0.0.1-SNAPSHOT.jar

### 빌드 및 실행
```shell
$ ./gradlew clean build
$ java -jar blog-search-application/build/libs/blog-search-api-0.0.1-SNAPSHOT.jar
```

## 블로그 검색 API 명세

Swagger UI url
```text 
http//localhost:8080/swagger-ui/index.html
```

### 블로그 검색
검색어를 통해 블로그를 검색

### Endpoint

```http
GET /blogs/search
```
### Request Parameters
| Parameter | Type     | Required | Default    | Description                                    |
|:----------|:---------|:---------|:-----------|:-----------------------------------------------|
| `query`   | `string` | `true`   |            | 검색어                                            |
| `page`    | `int`    | `false`  | `1`        | 페이지 번호                                         |
| `size`    | `int` | `false`  | `10`     | 페이지 사이즈                                        |
| `sort`    | `string` | `false`   | `ACCURACY`    | 정렬 방식(기본값: 정확도)<br/>ACCURACY(정확도), RECENT(최신순) |


### Responses

```json
{
  "page": 1,
  "size": 10,
  "totalCount": 1824844,
  "totalPage": 800,
  "contents": [
    {
      "blogName": "짧은머리 개발자",
      "title": "NestJS — <b>Test</b> Driven Development (1)",
      "description": "이전에 쓰던 To Do List를 폐기하고, NestJS MVC 환경에서 TDD를 수행하는 법을 작성하려 한다. 크게 Unit <b>Test</b>와 Integration <b>Test</b>로 나누어서 연재할 예정이다. 간략한 MVC 흔히 서비스의 프론트엔드에서 발생하는 요청을 처리하기 위해 우리는 백엔드의 시스템을 MVC 디자인 패턴을 이용해 설계하곤 한다. MVC 패턴을...",
      "url": "http://dev-whoan.xyz/102",
      "thumbnail": "https://search4.kakaocdn.net/argon/130x130_85_c/L9EJ0FzI9iO",
      "createdAt": "2023-03-10T21:59:55"
    }
  ]
}
```

### 인기 검색어 목록
사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공

### Endpoint

```http
GET /blogs/search/popular-keywords
```
### Request Parameters
| Parameter | Type     | Required | Default | Description           |
|:----------|:---------|:---------|:------|:----------------------|
| `size`    | `int` | `false`   | `10`     | 검색 키워드 수<br/>(최대 10개) |


### Responses
정렬: 검색 횟수 내림차순, 갱신일 내림차순
```json
[
  {
    "keyword": "카카오뱅크",
    "count": 486
  },
  {
    "keyword": "블로그",
    "count": 200
  }
]
```
# 사용한 외부 라이브러리
### Resilience4j
```text
- Retry 패턴을 적용하여 Kakao API 장애 시(HttpStatus 5xx) 재시도
- Kakao API 장애로 인해 재시도 횟수 초과 시 Circuit Breaker 패턴을 적용하여 서비스의 안정성을 확보하기 위해 Naver API를 호출 하도록 우회
```
### Caffeine
```text
- 캐시 라이브러리
- 검색 결과를 캐싱하여, 동일한 검색어에 대한 요청이 들어올 경우, 캐시된 결과를 반환
```
### Hazelcast
```text
- 동시성 문제를 해결하기 위해, 분산 락을 적용
```
### QueryDSL
```text
- 동적 쿼리를 위해 사용
```
