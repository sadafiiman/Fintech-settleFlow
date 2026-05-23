package com.settleFlow.modules.identity.service;

import com.settleFlow.modules.identity.model.User;
import com.settleFlow.modules.identity.repository.UserRepository;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    public void deactivate(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}