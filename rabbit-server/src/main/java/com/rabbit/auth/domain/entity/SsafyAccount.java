package com.rabbit.auth.domain.entity;

import com.rabbit.user.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name = "ssafy_accounts")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SsafyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ssafyAccountId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    private String userKey;

    @Column(nullable = false)
    private ZonedDateTime createdAt;
}
