import { useEffect, useState } from "react";
import getMetaMaskProvider from "../utils/getMetaMaskProvider";
import getWalletAddress from "../utils/getWalletAddress";

const useGetWallet = () => {
  const [address, setAddress] = useState<string | null>(null);
  useEffect(() => {
    const getWallet = async () => {
      const provider = await getMetaMaskProvider();
      if (!provider) {
        return;
      }
      const walletAddress = await getWalletAddress({ provider });
      if (!walletAddress || !walletAddress.address) {
        return;
      }
      setAddress(walletAddress.address);
      return;
    };
    getWallet();
  }, []);
  return { address };
};
export default useGetWallet;
