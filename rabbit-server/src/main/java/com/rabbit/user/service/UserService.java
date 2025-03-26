package com.rabbit.user.service;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.dto.response.LoginInfoResponseDTO;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public LoginInfoResponseDTO getLoginInfo(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 회원입니다"));

        return LoginInfoResponseDTO.builder()
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .build();
    }
}
