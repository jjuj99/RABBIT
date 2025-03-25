import { connectWallet } from "@/entities/wallet/utils";
import { useQuery } from "@tanstack/react-query";
import { getAccessToken } from "../utils/authUtils";

const useWalletConnection = () => {
  return useQuery({
    queryKey: ["wallet"],
    queryFn: async () => await connectWallet(),
    enabled: !!getAccessToken(),
  });
};

export default useWalletConnection;
