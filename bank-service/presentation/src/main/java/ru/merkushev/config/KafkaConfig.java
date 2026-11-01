package ru.merkushev.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import ru.merkushev.messages.RateRequestMessage;
import ru.merkushev.messages.RateResponseMessage;
import ru.merkushev.messages.RateUpdateMessage;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.rates-updates}")
    private String ratesUpdatesTopic;

    @Value("${app.kafka.topics.rates-requests}")
    private String ratesRequestsTopic;

    @Value("${app.kafka.topics.rates-replies}")
    private String ratesRepliesTopic;

    @Value("${app.kafka.request-timeout-ms}")
    private long requestTimeoutMs;

    @Bean
    public NewTopic ratesUpdatesTopic() {
        return TopicBuilder.name(ratesUpdatesTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ratesRequestsTopic() {
        return TopicBuilder.name(ratesRequestsTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ratesRepliesTopic() {
        return TopicBuilder.name(ratesRepliesTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public ConsumerFactory<String, RateUpdateMessage> rateUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank-service-rates-updates");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JacksonJsonDeserializer<RateUpdateMessage> valueDeserializer =
                new JacksonJsonDeserializer<>(RateUpdateMessage.class);
        valueDeserializer.addTrustedPackages("ru.merkushev.messages");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RateUpdateMessage> rateUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RateUpdateMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(rateUpdateConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, RateRequestMessage> rateRequestProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JacksonJsonSerializer<>()
        );
    }

    @Bean
    public ConsumerFactory<String, RateResponseMessage> rateResponseConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank-service-rates-replies");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JacksonJsonDeserializer<RateResponseMessage> valueDeserializer =
                new JacksonJsonDeserializer<>(RateResponseMessage.class);
        valueDeserializer.addTrustedPackages("ru.merkushev.messages");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, RateResponseMessage> ratesRepliesContainer() {
        ContainerProperties containerProperties = new ContainerProperties(ratesRepliesTopic);
        containerProperties.setGroupId("bank-service-rates-replies");

        return new ConcurrentMessageListenerContainer<>(
                rateResponseConsumerFactory(),
                containerProperties
        );
    }

    @Bean
    public ReplyingKafkaTemplate<String, RateRequestMessage, RateResponseMessage> replyingKafkaTemplate() {
        ReplyingKafkaTemplate<String, RateRequestMessage, RateResponseMessage> template =
                new ReplyingKafkaTemplate<>(
                        rateRequestProducerFactory(),
                        ratesRepliesContainer()
                );

        template.setDefaultReplyTimeout(Duration.ofMillis(requestTimeoutMs));
        return template;
    }
}