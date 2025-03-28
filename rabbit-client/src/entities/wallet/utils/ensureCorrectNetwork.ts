import { SEPOLIA_CHAIN_ID, SEPOLIA_NETWORK_PARAMS } from "../constant";

type NetworkResult = {
  success: boolean;
  error?:
    | "ALREADY_CONNECTED"
    | "USER_REJECTED_SWITCH"
    | "USER_REJECTED_ADD"
    | "NETWORK_ERROR";
};

// 올바른 네트워크 확인
export const ensureCorrectNetwork = async (): Promise<NetworkResult> => {
  try {
    const chainId = await window.ethereum?.request({ method: "eth_chainId" });

    if (chainId === SEPOLIA_CHAIN_ID) {
      return { success: true }; // 이미 올바른 네트워크
    }

    // 네트워크 전환 시도
    try {
      await window.ethereum?.request({
        method: "wallet_switchEthereumChain",
        params: [{ chainId: SEPOLIA_CHAIN_ID }],
      });
      return { success: true };
    } catch (error) {
      // 사용자가 전환을 거부한 경우
      if (error instanceof Error && "code" in error && error.code === 4001) {
        return { success: false, error: "USER_REJECTED_SWITCH" };
      }

      // 네트워크가 없는 경우 추가 시도
      if (error instanceof Error && "code" in error && error.code === 4902) {
        try {
          await window.ethereum?.request({
            method: "wallet_addEthereumChain",
            params: [SEPOLIA_NETWORK_PARAMS],
          });
          return { success: true };
        } catch (addError) {
          if (
            addError instanceof Error &&
            "code" in addError &&
            addError.code === 4001
          ) {
            return { success: false, error: "USER_REJECTED_ADD" };
          }
          return { success: false, error: "NETWORK_ERROR" };
        }
      }
      return { success: false, error: "NETWORK_ERROR" };
    }
  } catch (error) {
    console.error("네트워크 전환 실패:", error);
    return { success: false, error: "NETWORK_ERROR" };
  }
};

export default ensureCorrectNetwork;
