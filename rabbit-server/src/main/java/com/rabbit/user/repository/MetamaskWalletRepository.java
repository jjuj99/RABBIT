package com.rabbit.user.repository;

import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MetamaskWalletRepository extends JpaRepository<MetamaskWallet, Integer> {

    Optional<MetamaskWallet> findByWalletAddress(String walletAddress);

    Optional<MetamaskWallet> findByUser_UserIdAndPrimaryFlagTrue(Integer userId);

    Optional<MetamaskWallet> findByUserUserIdAndPrimaryFlagTrue(Integer userId);

    List<MetamaskWallet> findByPrimaryFlagTrue();

    Optional<MetamaskWallet> findByUserAndPrimaryFlagTrue(User user);

    @Query("SELECT w.walletAddress FROM MetamaskWallet w WHERE w.user.userId = :userId AND w.primaryFlag = true")
    Optional<String> findPrimaryWalletAddressByUserId(@Param("userId") Integer userId);
}
