package com.service.userservice.rabbitmq;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    // -> 검수 서비스 : 차량 정보 삭제
    public void sendMessageForDeletion(Long userId) {
        log.info("message send: userId is {}", userId);
        rabbitTemplate.convertAndSend("inspection-service-exchange", "car.delete", userId);
    }

    // <- 검수 서비스:
//    @RabbitListener(queues = "")
//    public List<CarDto> receiveMessageFromInspectionService() {
//
//        Message message = rabbitTemplate.receive("user-service-queue");
//
//        log.info("received message: {}", message);
//
//        if (message != null) {
//            List<CarDto> carDtoList = (List<CarDto>) message.getMessageProperties().getHeaders().get("carDtoList");
//            return carDtoList;
//        }
//
//        return null;
//    }

    // -> 검수 서비스 : 차량 정보 조회
    public void sendMessageForCarDto(Long userId) {
        log.info(">> request car dto list");
        rabbitTemplate.convertAndSend("inspection-service-exchange", "car.info.list", userId);
    }

    // -> 검수 서비스 : 테스트 데이터 저장 요청
    public void sendMessageForTestData(Long userId) {
        log.info(">> request test car data creation");
        rabbitTemplate.convertAndSend("inspection-service-exchange", "car.test.data.creation", userId);
    }
}
