package com.rabbit.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.ZonedDateTime;

@Table(name = "user_tokens")
@Entity
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private ZonedDateTime createdAt;
}
