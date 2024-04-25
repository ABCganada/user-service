package com.service.userservice.controller;

import com.service.userservice.domain.User;
import com.service.userservice.domain.UserRepository;
import com.service.userservice.domain.UserRole;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private RabbitMQService rabbitMQService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserExit() {
        Long userId = 123L;

        when(userService.deleteUser(userId)).thenReturn(true);
        ResponseEntity<?> response = userController.memberExit(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rabbitMQService, times(1)).sendMessageToInspectionService(userId);

        when(userService.deleteUser(userId)).thenReturn(false);
        response = userController.memberExit(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        response = userController.memberExit(null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUserDelete() {
        Long userId = 123L;

        User user = User.builder()
                .id(userId)
                .loginId(null)
                .nickname("testNickname")
                .provider(null)
                .providerId(null)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        when(userService.deleteUser(anyLong())).thenReturn(true);
        assertFalse(userRepository.findById(userId).isEmpty());

        userController.memberExit(userId);
        assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    public void carDataDeleteTest() {
        Long userId = 1233L;
        String carNumber = "ABC123";



    }
}
