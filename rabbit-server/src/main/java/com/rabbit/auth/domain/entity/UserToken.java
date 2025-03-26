package com.rabbit.auth.domain.entity;

import com.rabbit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name = "user_tokens")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private ZonedDateTime createdAt;
}
