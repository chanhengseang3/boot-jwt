package com.chs.springbootsecurity.controller;

import com.chs.springbootsecurity.data.MessageToProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sqs")
@RequiredArgsConstructor
public class SqsController {

    private static final String QUEUE_NAME = "cardSender";
    private static final String LISTENER = "TestListener";

    private final QueueMessagingTemplate queueMessagingTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendMessageToMessageProcessingQueue(@RequestBody MessageToProcess message) {
        log.info("Going to send message {} over SQS", message);
        this.queueMessagingTemplate.convertAndSend(QUEUE_NAME, message);
    }

    @SqsListener(value = LISTENER, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    private void receiveMessage(Object message) {
        log.info("Received SQS message {}", message);
    }

}