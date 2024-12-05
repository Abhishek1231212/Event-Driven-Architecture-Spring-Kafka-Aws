package com.app.event_driven;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;

@SpringBootApplication
@EnableSqs
public class ShippingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShippingApplication.class);
    }
}
