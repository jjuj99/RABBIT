package com.rabbit.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class TestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_user_id", nullable = false)
    private int testUserId;

    @Column(name = "name")
    private String name;

}
