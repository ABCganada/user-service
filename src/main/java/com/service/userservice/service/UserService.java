package com.service.userservice.service;

import com.service.userservice.domain.User;
import com.service.userservice.domain.UserRepository;
import com.service.userservice.dto.CarDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElse(null);
    }

    public boolean deleteUser(Long userId) {
        User user = getUserByUserId(userId);

        if (user != null) {
            userRepository.delete(user);
            return true;
        }

        return false;
    }

    public List<CarDto> userRequestCarInfo(Long userId) {
        log.info("유저 아이디: " + userId);
        List<CarDto> response = (List<CarDto>) rabbitTemplate.convertSendAndReceive("inspection-request-queue", userId);
        log.info("Received response from inspection service: " + response);
        return response;
    }
}
