package com.rabbit.auth.repository;

import com.rabbit.auth.domain.entity.TestUser;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestUserRepository extends JpaRepository<TestUser, Integer> {

    TestUser findById(int id);

    @NonNull
    List<TestUser> findAll();

}
