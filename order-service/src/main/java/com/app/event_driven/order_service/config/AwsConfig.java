package com.app.event_driven.order_service.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.access_key}")
    private String awsAccessKeyId;

    @Value("${aws.secret_key}")
    private String awsAccessSecretKey;
//
//    @Bean
//    public AmazonSQS sqsClient(){
//            AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsAccessSecretKey);
//            return AmazonSQSClientBuilder.standard()
//                    .withRegion(Regions.US_EAST_1)
//                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
//
//    }

    @Bean
    public AmazonSNS snsClient(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsAccessSecretKey);
        return AmazonSNSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
