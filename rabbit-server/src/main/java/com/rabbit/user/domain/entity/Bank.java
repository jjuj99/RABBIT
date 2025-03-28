package com.rabbit.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "banks")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankId;

    @Column(nullable = false)
    private String bankName;
}
