package com.rabbit.global.util;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

public class WalletAddressUtil {

    /**
     * 두 지갑 주소가 같은지 비교합니다.
     * @return 두 주소가 같으면 true, 다르면 false
     */
    public static boolean compareAddresses(String address1, String address2) {
        if (address1 == null || address2 == null) {
            return false;
        }

        // cleanHexPrefix를 통해 접두사(0x) 제거
        // 대소문자 무시하여 비교
        return Numeric.cleanHexPrefix(address1)
                .equalsIgnoreCase(Numeric.cleanHexPrefix(address2));
    }

    /**
     * 지갑 주소를 체크섬이 포함된 표준 형식으로 변환합니다.
     * @return 체크섬이 포함된 표준 형식의 주소
     */
    public static String toChecksumAddress(String address) {
        return Keys.toChecksumAddress(address);
    }
}
