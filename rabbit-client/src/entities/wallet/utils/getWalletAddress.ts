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

    const accounts = (await provider.request({
      method: "eth_accounts", // eth_requestAccounts 대신 eth_accounts 사용
    })) as string[];

    return { address: accounts[0] || null };
  } catch {
    return { address: null, error: "CONNECTION_ERROR" };
  }
};
export default getWalletAddress;
