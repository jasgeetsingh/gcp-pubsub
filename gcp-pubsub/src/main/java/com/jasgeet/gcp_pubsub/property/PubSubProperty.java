package com.jasgeet.gcp_pubsub.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("pubsub")
@Data
public class PubSubProperty {

  private TopicAndSubscriber source;
  private int maxRetryNumber;
  private int retryDelayInMilliseconds;
  private Boolean enableRetrySubscriber;
  private Boolean enableRetryPublisher;

  @Data
  public static class TopicAndSubscriber {
    private String topic;
    private String subscriber;
  }
}
