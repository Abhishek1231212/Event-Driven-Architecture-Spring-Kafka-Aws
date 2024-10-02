package com.app.event_driven.notification_service.service;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.app.event_driven.notification_service.model.Order;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationListener {

    @Autowired
    private AmazonSNS amazonSNS;

    @Autowired
    private AmazonSQS amazonSQS;

    @Value("${aws.sqs.payment.queueUrl}")
    private String paymentQueueUrl;

    @Value("${aws.sns.notification.topicArn}")
    private String topicArn;

    @Scheduled(fixedDelay = 5000)
    public void pollSqsMessages(){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(paymentQueueUrl);
        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        log.info("Notification messages:{}", messages.size());

        for(Message message: messages){
            Order order = new Gson().fromJson(message.getBody(), Order.class);
            sendNotification(order);
            amazonSQS.deleteMessage(new DeleteMessageRequest(paymentQueueUrl, message.getReceiptHandle()));
        }
    }

    private void sendNotification(Order order){
        String message = "Order " + order.getId() + ", Product " + order.getProductName() + " has been paid.";
        //log.info("TopicARN:{}", topicArn);
        PublishRequest publishRequest = new PublishRequest(topicArn, message);
        amazonSNS.publish(publishRequest);
    }

}
