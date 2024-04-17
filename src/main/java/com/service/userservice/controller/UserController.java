package com.service.userservice.controller;

import com.service.userservice.domain.User;
import com.service.userservice.dto.CarDto;
import com.service.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/info")
    public String userInfo(Model model, @CookieValue(name = "userId") Long userId) {
        model.addAttribute("loginType", "main");
        model.addAttribute("pageName", "Sandwich AI");

        User user = userService.getUserByUserId(userId);
        System.out.println(">> " + user);

        if (user == null) {
            return "redirect:http://localhost:8081/auth/login";
        }

        model.addAttribute("user", user);

        List<CarDto> carInfoList = userService.userRequestCarInfo(userId);
        model.addAttribute("carInfoList", carInfoList);

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
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed delete user request");
    }
}
