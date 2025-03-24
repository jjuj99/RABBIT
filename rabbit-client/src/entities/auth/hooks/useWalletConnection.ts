import { connectWallet } from "@/entities/wallet/utils";
import { useQuery } from "@tanstack/react-query";

const useWalletConnection = () => {
  return useQuery({
    queryKey: ["wallet"],
    queryFn: async () => await connectWallet(),
    enabled: false,
  });
};

export default useWalletConnection;
