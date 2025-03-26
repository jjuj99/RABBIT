package com.rabbit.auth.repository;

import com.rabbit.auth.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {

    Optional<UserToken> findByUser_UserId(Integer userId);
}
