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

    // 아이디 중복 체크
    public boolean existsUsername(String username) {
        return repo.existsByUsername(username);
    }

    // 전화번호 중복 체크
    public boolean existsPhone(String phone) {
        return repo.existsByPhone(phone);
    }

    // 이메일 중복 체크
    public boolean existsEmail(String email) {
        return email != null && repo.existsByEmail(email);
    }

    // 회원가입
    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword())); // 암호화
        return repo.save(user);
    }

    // 로그인
    public User login(String username, String rawPassword) {
        return repo.findByUsername(username)
                .filter(u -> encoder.matches(rawPassword, u.getPassword()))
                .orElse(null);
    }
}
