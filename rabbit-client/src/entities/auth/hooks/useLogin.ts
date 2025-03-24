import { useMutation, useQueryClient } from "@tanstack/react-query";
import { setAccessToken } from "../utils/authUtils";
import { LoginAPI } from "../api/authApi";
import { LoginRequest } from "../types/request";

const useLogin = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: LoginRequest) => {
      const res = await LoginAPI(data);
      return res.data;
    },
    onSuccess: (data) => {
      if (data) {
        setAccessToken(data.accessToken);
        queryClient.setQueryData(["auth", "user"], data.user);
      }
    },
  });
};
export default useLogin;
