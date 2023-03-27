package dev.dividendproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))                         // .entryTtl(Dur..)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())); //     // 캐시 TTL : Time To Live : 데이터의 유효기간
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }
    // 자바 직렬화 : Serialization : 데이터 or 오브젝트와 같은 값들을 바이트 형태로 변환

    // redis 서비스 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host);
        conf.setPort(this.port);
        // password가 있다면 password 설정 가능
        return new LettuceConnectionFactory(conf);
    }
}
