package com.service.userservice.rabbitmq;

import com.service.userservice.dto.CarDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageReceiver {

    private List<CarDto> carDtoList;

    @RabbitListener(queues = "dtoQ")
    public void receiveDtoQ(List<CarDto> carDtoList) {
        this.carDtoList = carDtoList;
    }

    public List<CarDto> waitForResponse() {
        while (carDtoList == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<CarDto> result = carDtoList;
        carDtoList = null;
        return result;
    }
}
