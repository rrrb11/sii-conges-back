package com.api.siiconges.service.user;

import com.api.siiconges.model.User;
import com.api.siiconges.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User update(User user) {
        return userRepository.save(user);
    }
}
