package com.georeference.services.sica.producers;

import com.georeference.dto.SicaProcessFileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SicaProducer {

    private static final String TOPIC = "georreference_procces";

    @Autowired
    private KafkaTemplate<String, SicaProcessFileDto> kafkaTemplate;

    public void sendMessage(SicaProcessFileDto message) {
        ProducerRecord<String, SicaProcessFileDto> sicaRecord = new ProducerRecord<>(TOPIC, message);
        log.info("Publicando mensaje en Kafka: {}", message.toString());
        this.kafkaTemplate.send(sicaRecord);
    }

}
