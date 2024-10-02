package com.app.event_driven.payment_service.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentListener {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AmazonSQS amazonSqs;

    @Value("${aws.sqs.order.queueUrl}")
    private String orderQueueUrl;

    @Scheduled(fixedDelay = 5000)
    public void pollSqsMessages(){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(orderQueueUrl);

        List<Message> messages = amazonSqs.receiveMessage(receiveMessageRequest).getMessages();
        log.info("Payment messages:{}",messages.size());
        for(Message message: messages){
            paymentService.processPayment(message.getBody());
            amazonSqs.deleteMessage(
                    new DeleteMessageRequest(orderQueueUrl, message.getReceiptHandle())
            );
            log.info("Message deleted");
        }
    }
}
