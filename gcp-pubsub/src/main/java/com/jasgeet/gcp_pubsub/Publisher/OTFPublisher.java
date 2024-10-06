package com.jasgeet.gcp_pubsub.Publisher;

import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import com.jasgeet.gcp_pubsub.property.PubSubProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OTFPublisher extends AbstractPubSubPublisher{

    private final PubSubProperty pubSubProperty;

    @Autowired
    protected OTFPublisher(
            PubSubTemplate pubSubTemplate,
            PubSubProperty pubSubProperty
    ) {
        super(pubSubTemplate);
        this.pubSubProperty = pubSubProperty;
    }

    @Override
    protected boolean enableRetryPublisher() {
        return false;
    }

    @Override
    public String getTopic() {
        return pubSubProperty.getSource().getTopic();
    }
}
