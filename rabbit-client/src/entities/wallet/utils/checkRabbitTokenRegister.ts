import getMetaMaskProvider from "./getMetaMaskProvider";

interface TokenRegistrationStatus {
  isRegistered: boolean;
  error?: string;
}

const checkRabbitTokenRegister = async (): Promise<TokenRegistrationStatus> => {
  const RABBIT_TOKEN_ADDRESS = import.meta.env.VITE_RABBIT_TOKEN_ADDRESS;
  const RABBIT_TOKEN_DECIMALS = import.meta.env.VITE_RABBIT_TOKEN_DECIMALS;
  const RABBIT_TOKEN_SYMBOL = import.meta.env.VITE_RABBIT_TOKEN_SYMBOL;

  const provider = await getMetaMaskProvider();
  if (!provider) {
    throw new Error("MetaMask provider를 찾을 수 없습니다.");
  }

  try {
    await provider.request({
      method: "wallet_watchAsset",
      params: {
        // @ts-expect-error 타입 추론 오류
        type: "ERC20",
        options: {
          address: RABBIT_TOKEN_ADDRESS,
          symbol: RABBIT_TOKEN_SYMBOL,
          decimals: RABBIT_TOKEN_DECIMALS,
        },
      },
    });

    // 새로 등록 성공
    return { isRegistered: true };
  } catch (error: unknown) {
    // 이미 등록된 토큰
    if (error instanceof Error && "code" in error && error.code === -32602) {
      return { isRegistered: true };
    }

    // 사용자가 거절
    if (error instanceof Error && "code" in error && error.code === 4001) {
      return {
        isRegistered: false,
        error: "사용자가 토큰 등록을 거절했습니다.",
      };
    }

    // 기타 에러
    if (error instanceof Error) {
      throw new Error(
        error.message || "토큰 등록 확인 중 에러가 발생했습니다.",
      );
    }
    throw new Error("토큰 등록 확인 중 에러가 발생했습니다.");
  }
};

export default checkRabbitTokenRegister;
