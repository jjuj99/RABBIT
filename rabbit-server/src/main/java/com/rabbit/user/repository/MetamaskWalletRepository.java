package com.rabbit.user.repository;

import com.rabbit.user.domain.entity.MetamaskWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetamaskWalletRepository extends JpaRepository<MetamaskWallet, Integer> {

    Optional<MetamaskWallet> findByWalletAddress(String walletAddress);

    Optional<MetamaskWallet> findByUser_UserIdAndPrimaryFlagTrue(Integer userId);

    Optional<MetamaskWallet> findByUserUserIdAndPrimaryFlagTrue(Integer userId);
}
