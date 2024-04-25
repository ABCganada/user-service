package com.service.userservice.service;

import com.service.userservice.domain.User;
import com.service.userservice.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

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
}
