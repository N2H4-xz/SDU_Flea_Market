package org.stnhh.sdu_flea_market.config;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.stnhh.sdu_flea_market.cache.impl.AppRedisCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {

    @Primary
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key/hashKey 用 String
        StringRedisSerializer stringSer = new StringRedisSerializer();
        template.setKeySerializer(stringSer);
        template.setHashKeySerializer(stringSer);

        // value/hashValue 用通用 JSON 序列化（更安全，免去 default typing 安全隐患）
        GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSer);
        template.setHashValueSerializer(jsonSer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public HashOperations<String, String, String> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    AppRedisCacheManager cache(RedisTemplate<String, Object> redisTemplate,
                               HashOperations<String, String, String> hashOperations) {
        return new AppRedisCacheManager(redisTemplate);
    }
}
