package com.webframework.safety404.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.webframework.safety404.domain.User;
import com.webframework.safety404.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean existsUsername(String username) {
        return repo.existsByUsername(username);
    }

    public boolean existsPhone(String phone) {
        return repo.existsByPhone(phone);
    }

    public boolean existsEmail(String email) {
        return email != null && repo.existsByEmail(email);
    }

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        return repo.save(user);
    }

    public User login(String username, String rawPassword) {
        return repo.findByUsername(username)
                .filter(u -> encoder.matches(rawPassword, u.getPassword()))
                .orElse(null);
    }
    
    public User findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
