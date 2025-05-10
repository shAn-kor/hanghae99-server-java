# 레디스 캐시 적용 보고서

## 캐싱 적용 항목
### 1. 콘서트
- 콘서트는 데이터가 자주 변하지 않는다.

### 2. 공연 일정
- 공연 일정도 데이터가 자주 변하지 않는다.

### 3. 포인트
- 포인트의 경우 충전하는 경우가 있으나 충전/사용이 조회보다 적다.

## 캐싱 적용 전략
### 1. 콘서트
- 캐시에 데이터 있으면 바로 반환, 없으면 디비에서 조회 후 캐싱

### 2. 공연 일정
- 캐시에 데이터 있으면 바로 반환, 없으면 디비에서 조회 후 캐싱

### 3. 포인트 
- 포인트 조회는 캐시에 데이터 있으면 바로 반환, 없으면 디비에서 조회 후 캐싱
- 포인트 충전/사용 시 '@CacheEvict' 로 캐시 만료 처리

## 레디스 캐시 설정
```aiignore
@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ LocalDateTime 지원 추가
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 8601 형식으로 출력

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(config)
                .build();
    }
}
```
- 직렬화 방식을 json 으로 변경해주었다.
- ObjectMappe 에 JSR-310 모듈 등록해 LocalDateTime 이 있는 객체의 직렬화를 가능하게 했다.

## 테스트
### 1. 콘서트
```aiignore
StopWatch 'Without Cache': 0.172179125 seconds
----------------------------------------------
Seconds       %       Task name
----------------------------------------------
0.172179125   100%    

StopWatch 'With Cache': 0.018091 seconds
----------------------------------------
Seconds       %       Task name
----------------------------------------
0.018091      100%    
```
- 총 100회 반복 기준 10배 정도 성능 차이 발생

### 2. 공연 일정
```aiignore
StopWatch 'Without Cache': 0.175151708 seconds
----------------------------------------------
Seconds       %       Task name
----------------------------------------------
0.175151708   100%    

StopWatch 'With Cache': 0.019522459 seconds
-------------------------------------------
Seconds       %       Task name
-------------------------------------------
0.019522459   100%    
```
- 총 100회 반복 기준 9배 정도 성능 차이 발생

### 3. 포인트
```aiignore
StopWatch 'Without Cache': 0.0688355 seconds
--------------------------------------------
Seconds       %       Task name
--------------------------------------------
0.0688355     100%    

StopWatch 'With Cache': 0.020146708 seconds
-------------------------------------------
Seconds       %       Task name
-------------------------------------------
0.020146708   100%    
```
- 총 100회 반복 기준 3.4배 정도 성능 차이 발생

## 결론
- Redis 캐시 적용으로 주요 API 응답 속도 향상 효과를 확인함.
- 캐시 전략을 각 기능에 맞게 적절히 사용했다.