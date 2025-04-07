import { useMutation } from "@tanstack/react-query";
import { GetNonceAPI } from "../api/authApi";

const useGetNonce = () => {
  return useMutation({
    mutationFn: async (walletAddress: string) => {
      return await GetNonceAPI(walletAddress);
    },
    onSuccess: (data) => {
      console.log(data);
    },
    onError: (error) => {
      console.log(error);
    },
  });
};

export default useGetNonce;
