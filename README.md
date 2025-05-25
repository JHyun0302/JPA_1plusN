# JPA 1+N 문제 정리

## 실습 환경
- 스프링 부트 3.4.4 
- Spring Data JPA 
- MySQL

## 프로젝트 설명
1. 연관 관계 구조 
- 연습용 연관 관계는 “국가” : “도시” = 1 : N 형태의 JOIN
- ex. “korea”라는 하나의 CountryEntity에 “seoul”, “busan”과 같은 CityEntity가 연관됩니다.
```markdown
korea : seoul, busan, daegu
france : paris
usa : newyork, chicago
```
---
## 1+N 발생 상황
**CountryEntity : CityEntity**

One쪽 조회
- One 조회 후 연관된 Many 접근 안함: 1번의 쿼리
- One 조회 후 연관된 Many 접근 함: One용 쿼리 한 번 +  List<CountryEntity>를 순회하며 각각의 연관된 CityEntity들을 확인할 쿼리 N번

Many쪽 조회 (주인)
- Many 조회 후 연관된 One 접근 안함: CityEntity 쿼리 한 번 및 mappedBy용 쿼리 한 번 (총 2번)
- Many 조회 후 연관된 One 접근 함: CityEntity 쿼리 한 번 및 mappedBy용 쿼리 한 번 (총 2번)

결과
- 연관 관계의 두 Entity를 JOIN 하는 경우 RDB SQL에서는 한 번의 구문으로 쿼리가 가능하지만, JPA는 각각의 Entity를 각각 조회

---
## Lazy, Eager
- Lazy 로딩 : 연관에 접근하지 않는다면 가져오지 않음
- Eager 로딩 : 연관에 접근하지 않아도 추가 쿼리로 먼저 가져옴

JPA JOIN별 default 값
- OneToOne : Eager 
- ManyToOne : Eager 
- OneToMany : Lazy

---
## 연관 관계 쿼리 단일화

추가 발생하는 N개 쿼리를 단건의 쿼리로 단일화 방법
- JPQL @Query로 JOIN FETCH 작성 
- QueryDSL로 fetchJoin()
- @EntityGraph 
- Lazy 쪽 조회시 Batch 설정을 통한 IN절

## join fetch, left join fetch

문제: JOIN FETCH 구문 작성시 누락되는 데이터가 존재
ex. CountryEntity에 물린 CityEntity가 없는 경우 findAll (JPQL로 작성)로 찾은 List<CountryEntity>에 해당 CountryEntity가 포함되지 않음

해결 방법
```jpaql
@Query("SELECT co FROM CountryEntity co " +
    "JOIN FETCH co.cityEntities ci")
    List<CountryEntity> findAllFetch();
    
// 아래처럼 변경
@Query("SELECT co FROM CountryEntity co " +
    "LEFT JOIN FETCH co.cityEntities ci")
    List<CountryEntity> findAllFetch();
```

JOIN FETCH 
- INNER JOIN으로 연관된게 있는 교집합이어야 조회 됨

LEFT JOIN FETCH
- LEFT OUTER JOIN으로 부모 + 교집합이면 조회 됨

---
## 다중 OneToMany 문제
- 하나의 Entity에 대해 OneToMany가 2개 이상 들어갈 경우

FETCH 조회시 문제
- 2개 이상의 OneToMany를 FETCH 조회할 경우
```jpaql
@Query("SELECT co FROM CountryEntity co " +
    "LEFT JOIN FETCH co.cityEntities ci " +
    "LEFT JOIN FETCH co.religionEntities re")
    List<CountryEntity> findAllFetch();
```

문제: JPA 구현체 중 하나인 Hibernate는 2개 이상의 OneToMany fetch를 강제로 막음.
이유: OneToMany fetch로 가지고 오면 카티션 곱으로 가져와 중복이 발생! 중복을 제거하는 조건이 까다로워 이 자체를 막음.

해결 방법
1. Set (비권장)
   1. Set은 순서 보장 불가 
   2. Set을 호출하며 발생하는 중복 제거 체킹 자원 발생 
   3. 정확한 Id기반 hashCode equals 구현 필요

2. 한쪽만 fetch 나머지 in절
    - 우선 한쪽 OneToMany는 기존과 같이 fetch로 유지
    - 나머지쪽은 단순 Lazy로 두면 1+N 문제가 동일하게 발생하기 때문에 Batch 설정을 통한 IN 절 처리를 수행 `@BatchSize(size = 250)`

---
## 페이지네이션 문제
문제: One쪽 Entity 기준으로 OneToMany fetch시 페이지네이션을 함께 수행하면 아래와 같은 경고가 발생
```log
WARN 1496 --- [1plusNtest] [nio-8080-exec-5] org.hibernate.orm.query                  : HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
```
위 WARN 경고는 페이지네이션을 SQL : LIMIT OFFSET 절로 수행하는 것이 아니라, 스프링 측에 모든 데이터를 다 가져온 뒤 진행한다는 의미 자원 낭비)

해결 방법: batch in절
- 1+N 개의 쿼리 중 N개의 쿼리에 대해 batch 단위로 묶어 1+소수개의 쿼리로 성능을 최적화 하는 방법 `@BatchSize(size = 250)`

## 정리

1. 단순하게 List<One> 목록만 보여주고 연관된 Many 접근 안함
   - 접근 안하기 때문에 Lazy 로딩 사용해도 무방

2. List<One> 목록 및 각 연관 Many 접근 함
   1. `페이지네이션이 들어감`
      - @BatchSize를 통한 IN절 쿼리 수행 
   2. `다중 OneToMany 상황에서 각각의 OneToMany 데이터가 다 필요함`
      - 하나만 join fetch 나머지는 @BatchSize를 통한 IN절 쿼리 수행 또는 전체 @BatchSize를 통한 IN절 쿼리 수행 
   3. `SQL 조건으로 가져온 데이터가 다 필요한 경우` 또는 `List<One>의 size가 작고 연관 접근 케이스` 
      - join fetch 사용

---
## 출처: https://www.devyummi.com/page?id=67f6e150c646e7518b74308d


