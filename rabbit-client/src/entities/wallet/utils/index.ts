import type { EIP1193Provider } from "@/vite-env";

export const getMetaMaskProvider = async () => {
  if (typeof window !== "undefined" && typeof window.ethereum !== "undefined") {
    if (window.ethereum.providers) {
      return window.ethereum.providers.find(
        (p: EIP1193Provider) => p.isMetaMask,
      );
    }
    return window.ethereum;
  }
  return null;
};

export const connectWallet = async () => {
  try {
    const provider = await getMetaMaskProvider();
    if (!provider) {
      throw new Error("MetaMask가 설치되어 있지 않습니다.");
    }

    const accounts = (await provider.request({
      method: "eth_requestAccounts",
    })) as string[];

    return { address: accounts[0] };
  } catch (error) {
    console.error("MetaMask 연결 중 오류 발생:", error);
    throw error;
  }
};

export const formatBalance = (rawBalance: string) => {
  const balance = (parseInt(rawBalance) / 1000000000000000000).toFixed(2);
  return balance;
};

export const formatChainAsNum = (chainIdHex: string) => {
  const chainIdNum = parseInt(chainIdHex);
  return chainIdNum;
};

export const formatAddress = (addr: string) => {
  const upperAfterLastTwo = addr.slice(0, 2) + addr.slice(2);
  return `${upperAfterLastTwo.substring(0, 5)}...${upperAfterLastTwo.substring(39)}`;
};
