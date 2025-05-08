package ru.melowetty.notificationservice.config

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration {
     @Bean
     fun cacheManager(): CacheManager {
         val cacheManager = CaffeineCacheManager()
         cacheManager.setCaffeine(
             Caffeine.newBuilder()
                 .expireAfterWrite(15, TimeUnit.MINUTES)
                 .maximumSize(5000)
         )
         return cacheManager
     }
}