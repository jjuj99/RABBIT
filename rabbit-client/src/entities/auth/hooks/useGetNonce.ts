import { useMutation } from "@tanstack/react-query";
import { GetNonceAPI } from "../api/authApi";

const useGetNonce = () => {
  return useMutation({
    mutationFn: async (walletAddress: string) => {
      return await GetNonceAPI(walletAddress);
    },
  });
};

export default useGetNonce;
