package ru.melowetty.notificationservice.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import ru.melowetty.notificationservice.model.ExternalNotification


@Configuration
@EnableKafka
class NotificationListenerConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return props
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, ExternalNotification> {
        return DefaultKafkaConsumerFactory(
            consumerConfigs(),
            StringDeserializer(),
            JsonDeserializer(ExternalNotification::class.java, false)
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ExternalNotification> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, ExternalNotification> =
            ConcurrentKafkaListenerContainerFactory<String, ExternalNotification>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}