package com.jasgeet.gcp_pubsub.Subscriber;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.pubsub.v1.PubsubMessage;
import com.jasgeet.gcp_pubsub.MyRuntimeException;
import com.jasgeet.gcp_pubsub.Payload;
import com.jasgeet.gcp_pubsub.Publisher.AbstractPubSubPublisher;
import com.jasgeet.gcp_pubsub.Publisher.OTFPublisher;
import com.jasgeet.gcp_pubsub.property.PubSubProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MySourceSubscriber extends AbstractPubSubSubscriber {

  private final PubSubProperty pubSubProperty;
  private final ObjectMapper objectMapper;
  private final OTFPublisher mySourcePublisher;

  public MySourceSubscriber(
      PubSubTemplate pubSubTemplate,
      PubSubProperty pubSubProperty,
      ObjectMapper objectMapper,
      OTFPublisher mySourcePublisher
  ) {
    super(pubSubTemplate);
    this.pubSubProperty = pubSubProperty;
    this.objectMapper = objectMapper;
    this.mySourcePublisher = mySourcePublisher;
  }

  @Override
  public String getSubscription() {
    return pubSubProperty.getSource().getSubscriber();
  }



  @Override
  public AbstractPubSubPublisher getSourcePublisher() {
    return mySourcePublisher;
  }

  @Override
  public int getMaxRetryNumber() {
    return pubSubProperty.getMaxRetryNumber();
  }

  @Override
  public int getRetryDelayDurationInMs() {
    return pubSubProperty.getRetryDelayInMilliseconds();
  }

  @Override
  public boolean isEnableRetry() {
    return pubSubProperty.getEnableRetrySubscriber();
  }

  @Override
  protected void receive(BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage) {
    final PubsubMessage message = basicAcknowledgeablePubsubMessage.getPubsubMessage();
    try {
      Payload payload = objectMapper.readValue(message.getData().toStringUtf8(), Payload.class);

        log.info("Message received from subscriber {} with value {}", getSubscription(), payload);
        basicAcknowledgeablePubsubMessage.ack();

    } catch (Exception e) {
      log.error("Error consuming message from subscriber {}", getSubscription(), e);
      if (isTrue(pubSubProperty.getEnableRetrySubscriber())) {
        throw new MyRuntimeException(e);
      } else {
        basicAcknowledgeablePubsubMessage.nack();
      }
    }
  }
}
