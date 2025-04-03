import Web3 from "web3";

export const createWeb3 = (): Web3 | null => {
  if (typeof window !== "undefined" && typeof window.ethereum !== "undefined") {
    let provider = null;

    if (window.ethereum.providers) {
      provider = window.ethereum.providers.find((p) => p.isMetaMask);
    } else {
      provider = window.ethereum;
    }

    if (provider) {
      return new Web3(provider);
    }
  }
  return null;
};
