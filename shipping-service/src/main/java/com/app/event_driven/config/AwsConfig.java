package com.app.event_driven.config;



import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;

import java.util.Collections;

@Configuration
public class AwsConfig {
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(){
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setWaitTimeOut(10);
        return factory;
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(@Qualifier("amazonSQSAsync") AmazonSQSAsync amazonSQSAsync){
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        factory.setAmazonSqs(amazonSQSAsync);
        factory.setArgumentResolvers(Collections.singletonList(
                new PayloadMethodArgumentResolver(new MappingJackson2MessageConverter())
        ));
        return factory.createQueueMessageHandler();
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.defaultClient();
    }

}
