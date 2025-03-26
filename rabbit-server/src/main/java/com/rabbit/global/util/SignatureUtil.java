package com.rabbit.global.util;

import org.springframework.stereotype.Component;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static java.util.Arrays.copyOfRange;

@Component
public class SignatureUtil {

    public String recoverAddress(String signature, String nonce) {
        try {
            // 1. personal_sign 규격에 맞게 메시지에 prefix 붙이기
            // 메타마스크에서 서명할 때 자동으로 이 prefix를 붙여서 처리함
            String prefix = "\u0019Ethereum Signed Message:\n" + nonce.length();
            String prefixedMessage = prefix + nonce;

            // 2. prefix가 붙은 메시지를 Keccak-256으로 해싱 (이더리움 표준 해시 방식)
            byte[] msgHash = Hash.sha3(prefixedMessage.getBytes());

            // 3. signature는 65바이트 (r: 32, s: 32, v: 1)
            // Hex 문자열을 바이트 배열로 변환 (0x 생략 가능)
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);

            // 4. 메타마스크에서 반환되는 v는 0 또는 1일 수 있음 → ECDSA는 27 또는 28 필요
            byte v = signatureBytes[64];
            if (v < 27) v += 27;

            // 5. 서명을 r, s, v로 나누어 SignatureData 객체로 구성
            Sign.SignatureData sigData = new Sign.SignatureData(
                    v,
                    copyOfRange(signatureBytes, 0, 32),   // r
                    copyOfRange(signatureBytes, 32, 64)  // s
            );

            // 6. 해시된 메시지와 서명 데이터를 이용해 공개키 복원
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(nonce.getBytes(), sigData);

            // 7. 공개키를 이더리움 주소로 변환 (Keccak-256 해싱 후 마지막 20바이트)
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            return recoveredAddress;
        } catch (Exception e) {
            throw new IllegalArgumentException("서명 복원 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
