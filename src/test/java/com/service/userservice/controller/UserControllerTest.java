package com.service.userservice.controller;

import com.service.userservice.domain.User;
import com.service.userservice.domain.UserRepository;
import com.service.userservice.domain.UserRole;
import com.service.userservice.dto.CarDto;
import com.service.userservice.rabbitmq.RabbitMQReceiver;
import com.service.userservice.rabbitmq.RabbitMQService;
import com.service.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private RabbitMQReceiver rabbitMQReceiver;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void dataConsistencyTest() {
        for (int i = 0; i < 10000; i++) {
            //데이터 생성
            User user = createUser();
            userRepository.save(user);

            rabbitMQService.sendMessageForTestData(user.getId());

            rabbitMQService.sendMessageForCarDto(user.getId());
            CompletableFuture<List<CarDto>> completableFuture = rabbitMQReceiver.getCarDtoListFuture();
            completableFuture.thenAccept(carDtoList -> {
                assertThat(carDtoList.size()).isEqualTo(1);
                assertThat(carDtoList.get(0).getCarNumber()).isEqualTo("testCar");
            });

            //데이터 삭제

        }
    }


    private User createUser() {
        return User.builder()
                .nickname("testUser")
                .role(UserRole.USER)
                .build();
    }
}
