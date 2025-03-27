import type { EIP1193Provider } from "@/vite-env";

type WalletConnectionResult = {
  address: string | null;
  error?: "PROVIDER_NOT_FOUND" | "USER_REJECTED" | "CONNECTION_ERROR";
};

// 지갑 연결하는 메서드
export const connectWallet = async ({
  provider,
}: {
  provider: EIP1193Provider;
}): Promise<WalletConnectionResult> => {
  try {
    if (!provider) {
      return { address: null, error: "PROVIDER_NOT_FOUND" };
    }

    const accounts = (await provider.request({
      method: "eth_requestAccounts",
    })) as string[];

    return { address: accounts[0] };
  } catch (error) {
    console.error("MetaMask 연결 중 오류 발생:", error);

    if (error instanceof Error && "code" in error && error.code === 4001) {
      return { address: null, error: "USER_REJECTED" };
    }

    return { address: null, error: "CONNECTION_ERROR" };
  }
};

export default connectWallet;
