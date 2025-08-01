package com.fiap.pedido.config.kafka;

import com.fiap.pedido.dto.request.PedidoRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
    }

    @Test
    void deveCriarConsumerFactory() {
        ConsumerFactory<String, PedidoRequestDTO> result = kafkaConfig.consumerFactory();

        assertNotNull(result);
        assertTrue(result instanceof DefaultKafkaConsumerFactory);
    }

    @Test
    void deveConfigurarPropriedadesConsumerFactory() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();
        Map<String, Object> props = consumerFactory.getConfigurationProperties();

        assertEquals("kafka:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("pedido-service", props.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class.getName(), props.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(JsonDeserializer.class.getName(), props.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("*", props.get(JsonDeserializer.TRUSTED_PACKAGES));
        assertEquals(PedidoRequestDTO.class, props.get(JsonDeserializer.VALUE_DEFAULT_TYPE));
        assertEquals(false, props.get(JsonDeserializer.USE_TYPE_INFO_HEADERS));
    }

    @Test
    void deveCriarPedidoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PedidoRequestDTO> result =
                kafkaConfig.pedidoKafkaListenerContainerFactory();

        assertNotNull(result);
        assertNotNull(result.getConsumerFactory());
    }

    @Test
    void deveConfigurarConsumerFactoryNaListenerFactory() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();
        ConcurrentKafkaListenerContainerFactory<String, PedidoRequestDTO> listenerFactory =
                kafkaConfig.pedidoKafkaListenerContainerFactory();

        assertNotNull(listenerFactory.getConsumerFactory());
        assertEquals(consumerFactory.getClass(), listenerFactory.getConsumerFactory().getClass());
    }

    @Test
    void deveValidarTipoDoConsumerFactory() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();

        assertTrue(consumerFactory instanceof DefaultKafkaConsumerFactory);
    }

    @Test
    void deveValidarConfiguracoesEspecificasDoJsonDeserializer() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();
        Map<String, Object> props = consumerFactory.getConfigurationProperties();

        assertTrue(props.containsKey(JsonDeserializer.TRUSTED_PACKAGES));
        assertTrue(props.containsKey(JsonDeserializer.VALUE_DEFAULT_TYPE));
        assertTrue(props.containsKey(JsonDeserializer.USE_TYPE_INFO_HEADERS));

        assertEquals("*", props.get(JsonDeserializer.TRUSTED_PACKAGES));
        assertEquals(PedidoRequestDTO.class, props.get(JsonDeserializer.VALUE_DEFAULT_TYPE));
        assertFalse((Boolean) props.get(JsonDeserializer.USE_TYPE_INFO_HEADERS));
    }

    @Test
    void deveValidarBootstrapServersConfig() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();
        Map<String, Object> props = consumerFactory.getConfigurationProperties();

        assertTrue(props.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("kafka:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    void deveValidarGroupIdConfig() {
        ConsumerFactory<String, PedidoRequestDTO> consumerFactory = kafkaConfig.consumerFactory();
        Map<String, Object> props = consumerFactory.getConfigurationProperties();

        assertTrue(props.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("pedido-service", props.get(ConsumerConfig.GROUP_ID_CONFIG));
    }
}