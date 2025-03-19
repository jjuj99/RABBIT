package com.rabbit.auth.service;

import com.rabbit.auth.domain.entity.TestUser;
import com.rabbit.auth.repository.TestUserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestUserService {

    @Autowired
    public final TestUserRepository testUserRepository;

    public TestUserService(TestUserRepository testUserRepository) {
        this.testUserRepository = testUserRepository;
    }

    public TestUser getUserById(int id) {
        return testUserRepository.findById(id);
    }

    public List<TestUser> getAllUsers() {
        return testUserRepository.findAll();
    }


}
