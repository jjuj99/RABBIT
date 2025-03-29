import { useEffect, useState } from "react";
import getMetaMaskProvider from "../utils/getMetaMaskProvider";
import getWalletAddress from "../utils/getWalletAddress";
import getRabbitBalance from "../utils/getRabbitBalance";

const useGetBalance = () => {
  const [balance, setBalance] = useState(0);
  useEffect(() => {
    const getBalance = async () => {
      const provider = await getMetaMaskProvider();
      if (!provider) {
        return;
      }
      const walletAddress = await getWalletAddress({ provider });
      if (!walletAddress || !walletAddress.address) {
        return;
      }
      const balance = await getRabbitBalance(walletAddress.address);
      setBalance(balance);
    };
    getBalance();
  }, []);
  console.log(balance);

  return { balance };
};
export default useGetBalance;
