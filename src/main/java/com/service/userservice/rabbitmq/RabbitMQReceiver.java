package com.service.userservice.rabbitmq;

import com.service.userservice.dto.CarDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class RabbitMQReceiver {

    private CompletableFuture<List<CarDto>> completableFuture;

    @Async
    @RabbitListener(queues = "user-car-info-queue")
    public void receiveDtoQ(List<CarDto> carDtoList) {
        if (completableFuture != null && !completableFuture.isDone()) {
            completableFuture.complete(carDtoList);
        }
    }

    public CompletableFuture<List<CarDto>> getCarDtoListFuture() {
        completableFuture = new CompletableFuture<>();
        return completableFuture;
    }
}
