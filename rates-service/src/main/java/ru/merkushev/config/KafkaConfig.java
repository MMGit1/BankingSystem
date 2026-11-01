package ru.merkushev.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import ru.merkushev.messages.RateRequestMessage;
import ru.merkushev.messages.RateResponseMessage;
import ru.merkushev.messages.RateUpdateMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topics.rates-updates}")
    private String ratesUpdatesTopic;

    @Value("${app.kafka.topics.rates-requests}")
    private String ratesRequestsTopic;

    @Value("${app.kafka.topics.rates-replies}")
    private String ratesRepliesTopic;

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
    public ProducerFactory<String, RateUpdateMessage> rateUpdateProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, RateUpdateMessage> rateUpdateKafkaTemplate(
            ProducerFactory<String, RateUpdateMessage> rateUpdateProducerFactory
    ) {
        return new KafkaTemplate<>(rateUpdateProducerFactory);
    }

    @Bean
    public ProducerFactory<String, RateResponseMessage> rateResponseProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, RateResponseMessage> rateResponseKafkaTemplate(
            ProducerFactory<String, RateResponseMessage> rateResponseProducerFactory
    ) {
        return new KafkaTemplate<>(rateResponseProducerFactory);
    }

    @Bean
    public ConsumerFactory<String, RateRequestMessage> rateRequestConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "ru.merkushev.messages");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, "ru.merkushev.messages.RateRequestMessage");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RateRequestMessage> rateRequestKafkaListenerContainerFactory(
            ConsumerFactory<String, RateRequestMessage> rateRequestConsumerFactory,
            KafkaTemplate<String, RateResponseMessage> rateResponseKafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, RateRequestMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(rateRequestConsumerFactory);
        factory.setReplyTemplate(rateResponseKafkaTemplate);
        return factory;
    }
}