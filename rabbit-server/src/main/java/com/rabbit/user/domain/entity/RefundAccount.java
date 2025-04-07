package com.rabbit.user.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name = "refund_accounts")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class RefundAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer refundAccountId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer bankId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Boolean primaryFlag;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;
}
