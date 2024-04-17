package com.service.userservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 유저_데이터_테스트() {
        //given
        List<User> userList = userRepository.findAll();

        //when
        User targetUser = null;
        for (User user : userList) {
            if (user.getId().equals(3L)) {
                targetUser = user;
                break;
            }
        }

        //then
        assertTrue(targetUser.getNickname().equals("구글신민기"));
    }
}
