package com.rabbit.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

@Slf4j
@Component
public class SignatureUtil {

    /**
     * nonce 생성
     */
    public static String createNonce() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    /**
     * 서명된 메시지에서 지갑 주소를 복원합니다.
     */
    public static String recoverAddress(String signature, String nonce) {
        try {
            // 서명 데이터에서 v, r, s 분리
            String cleanSig = Numeric.cleanHexPrefix(signature);
            byte[] signatureBytes = Numeric.hexStringToByteArray(cleanSig);

            // r: 첫 32바이트, s: 다음 32바이트, v: 마지막 1바이트
            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
            byte v = signatureBytes[64];

            // v 값 보정 (MetaMask에서는 27/28 또는 0/1)
            if (v < 27) {
                v += 27;
            }

            // 원본 메시지 재생성
            log.info("원본 메시지: {}", nonce);

            // 이더리움 서명 메시지 형식으로 변환 (EIP-191 표준)
            byte[] msgHash = getEthereumMessageHash(nonce.getBytes(StandardCharsets.UTF_8));

            // 서명 데이터로 SignatureData 객체 생성
            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

            // 공개키 복원 및 주소 추출
            BigInteger publicKey = Sign.signedMessageHashToKey(msgHash, signatureData);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            log.info("복원된 주소: {}", recoveredAddress);
            return recoveredAddress;
        } catch (Exception e) {
            log.error("서명 복원 중 오류 발생: ", e);
            return null;
        }
    }

    /**
     * EIP-191 표준에 따라 이더리움 메시지 해시 생성
     */
    private static byte[] getEthereumMessageHash(byte[] message) {
        // '\x19Ethereum Signed Message:\n' + messageLength + message
        String prefix = "\u0019Ethereum Signed Message:\n" + message.length;
        byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);

        // 프리픽스와 메시지 합치기
        byte[] prefixedMessage = new byte[prefixBytes.length + message.length];
        System.arraycopy(prefixBytes, 0, prefixedMessage, 0, prefixBytes.length);
        System.arraycopy(message, 0, prefixedMessage, prefixBytes.length, message.length);

        // Keccak-256 해시 적용
        return Hash.sha3(prefixedMessage);
    }

}
