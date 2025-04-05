import { useSuspenseQuery } from "@tanstack/react-query";
import getMetaMaskProvider from "../utils/getMetaMaskProvider";
import getWalletAddress from "../utils/getWalletAddress";

const useGetWallet = () => {
  const { data: address } = useSuspenseQuery({
    queryKey: ["user", "wallet"],
    queryFn: async () => {
      const provider = await getMetaMaskProvider();
      if (!provider) {
        return null;
      }
      const walletAddress = await getWalletAddress({ provider });
      return walletAddress?.address || null;
    },
  });

  return { address };
};

export default useGetWallet;
