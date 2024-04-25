package com.service.userservice.rabbitmq;

import com.service.userservice.dto.CarDto;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQService {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    // -> 검수 서비스 : 차량 정보 삭제
    public void sendMessageToInspectionService(Long userId) {
        log.info("message send: userId is {}", userId);
        rabbitTemplate.convertAndSend("inspection-service-exchange", routingKey, userId);
    }

    // <- 검수 서비스:
    @RabbitListener(queues = "user-service-queue")
    public List<CarDto> receiveMessageFromInspectionService() {

        Message message = rabbitTemplate.receive("user-service-queue");

        log.info("received message: {}", message);

        if (message != null) {
            List<CarDto> carDtoList = (List<CarDto>) message.getMessageProperties().getHeaders().get("carDtoList");
            return carDtoList;
        }

        return null;
    }

    // -> 검수 서비스 : 차량 정보 조회
    public void requestCarDtoList(Long userId) {
        log.info(">> request car dto list");
        rabbitTemplate.convertAndSend("inspection-service-exchange", "car-dto-list-routing-key", userId);
    }
}
