package com.rabbit.auth.repository;

import com.rabbit.auth.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {

}
