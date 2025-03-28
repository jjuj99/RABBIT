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
export default getMetaMaskProvider;
