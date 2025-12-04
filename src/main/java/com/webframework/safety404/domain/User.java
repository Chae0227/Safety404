package com.webframework.safety404.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 자동 증가 회원번호

    @Column(nullable = false, unique = true, length = 30)
    private String username;   // 아이디

    @Column(nullable = false)
    private String password;   // 암호화된 비밀번호

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String email;      // 선택값

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    private LocalDate birth;

    private String address;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
