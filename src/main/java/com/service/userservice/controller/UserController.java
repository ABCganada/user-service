package com.service.userservice.controller;

import com.service.userservice.domain.User;
import com.service.userservice.dto.CarDto;
import com.service.userservice.rabbitmq.MessageReceiver;
import com.service.userservice.rabbitmq.RabbitMQService;
import com.service.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RabbitMQService rabbitMQService;
    private final MessageReceiver messageReceiver;

    @GetMapping("/info/{userId}")
    public String userInfo(Model model, @PathVariable("userId") Long userId) {
        model.addAttribute("loginType", "main");
        model.addAttribute("pageName", "Sandwich AI");

        User user = userService.getUserByUserId(userId);
        System.out.println(">> " + user);

        if (user == null) {
            return "redirect:http://localhost:8081/auth/login";
        }

        model.addAttribute("user", user);

        rabbitMQService.requestCarDtoList(userId);

        CompletableFuture<List<CarDto>> carDtoListFuture = messageReceiver.waitForResponse();

        carDtoListFuture.thenAccept(carDtoList -> {
            model.addAttribute("carDtoList", carDtoList);
        }).join();

        return "info";
    }

    @ResponseBody
    @DeleteMapping("/exit")
    public ResponseEntity<?> memberExit(@CookieValue(name = "userId") Long userId) {

        if (userId == null) {
            log.info("user id가 쿠키에 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("token in cookies not found");
        }

        boolean success = userService.deleteUser(userId);

        if (success) {
            log.info("delete user successful");
            rabbitMQService.sendMessageToInspectionService(userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed delete user request");

    }

    @ResponseBody
    @GetMapping("mq/test")
    public CompletableFuture<ResponseEntity<?>> mqTest(@CookieValue(name = "userId") Long userId) {
        rabbitMQService.requestCarDtoList(userId);

        return messageReceiver.waitForResponse().thenApply(ResponseEntity::ok);
    }
}
