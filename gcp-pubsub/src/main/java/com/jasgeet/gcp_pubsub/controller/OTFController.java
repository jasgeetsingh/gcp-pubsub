package com.jasgeet.gcp_pubsub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasgeet.gcp_pubsub.Payload;
import com.jasgeet.gcp_pubsub.Publisher.OTFPublisher;
import com.jasgeet.gcp_pubsub.property.PubSubProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.AcknowledgeablePubsubMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
@RequiredArgsConstructor
@RestController
public class OTFController {

    private final OTFPublisher otfPublisher;
    private final ObjectMapper objectMapper;
    private final PubSubTemplate pubSubTemplate;
    private final PubSubProperty pubSubProperty;

    @PostMapping("/publish-source")
    public Boolean publishMessage(@RequestBody Payload payload) {
        String payloadAsString;
        try {
            payload.setId(UUID.randomUUID());
            payloadAsString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        otfPublisher.publish(payloadAsString, Map.of());

        return true;
    }


    @GetMapping("/source")
    public List<String> pullDataFromSourceTopic() {
        final List<AcknowledgeablePubsubMessage> messages =
                pubSubTemplate.pull(
                        pubSubProperty.getSource().getSubscriber(),
                        100,
                        true
                );

        return messages.stream().map(Object::toString).collect(toList());
    }


}
