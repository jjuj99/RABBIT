import { ethers } from "ethers";
import type { EIP1193Provider } from "@/vite-env";

type WalletAddressResult = {
  address: string | null;
  error?: "PROVIDER_NOT_FOUND" | "USER_REJECTED" | "CONNECTION_ERROR";
};

const getWalletAddress = async ({
  provider,
}: {
  provider: EIP1193Provider;
}): Promise<WalletAddressResult> => {
  try {
    if (!provider) {
      return { address: null, error: "PROVIDER_NOT_FOUND" };
    }

    // 최신 ethers.js API 사용
    const ethersProvider = new ethers.BrowserProvider(provider);
    const signer = await ethersProvider.getSigner();

    // 지갑 연결 요청
    const accounts = await signer.getAddress();

    return { address: accounts || null };
  } catch (error) {
    if (error instanceof Error && "code" in error && error.code === 4001) {
      // 사용자가 요청을 거부한 경우
      return { address: null, error: "USER_REJECTED" };
    }
    return { address: null, error: "CONNECTION_ERROR" };
  }
};

export default getWalletAddress;
