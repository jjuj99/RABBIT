import {
  GetBankListAPI,
  Send1wonAPI,
  Verify1wonAPI,
} from "@/entities/account/api/accountApi";
import {
  Send1wonRequest,
  Verify1wonRequest,
} from "@/entities/account/types/request";
import { useMutation, useQuery } from "@tanstack/react-query";
import {
  CheckEmailAPI,
  CheckNicknameAPI,
  PermitCoinAPI,
  PermitCoinRequest,
  SignupAPI,
} from "../api/authApi";
import { SignUpRequest } from "../types/schema";
import { toast } from "sonner";

const useSignup = () => {
  const { data: bankList } = useQuery({
    queryKey: ["bankList"],
    queryFn: async () => {
      try {
        const res = await GetBankListAPI();
        return res.data;
      } catch (error) {
        if (error instanceof Error) {
          toast.error(error.message);
        } else {
          toast.error("은행 목록을 불러오는데 실패했습니다");
        }
      }
    },
  });
  const { mutateAsync: send1won } = useMutation({
    mutationFn: (data: Send1wonRequest) => Send1wonAPI(data),
  });
  const { mutateAsync: verify1won } = useMutation({
    mutationFn: (data: Verify1wonRequest) => Verify1wonAPI(data),
  });
  const { mutateAsync: checkNickname } = useMutation({
    mutationFn: (nickname: string) => CheckNicknameAPI(nickname),
  });
  const { mutateAsync: signup } = useMutation({
    mutationFn: (data: SignUpRequest) => SignupAPI(data),
  });
  const { mutateAsync: checkEmail } = useMutation({
    mutationFn: (email: string) => CheckEmailAPI(email),
  });
  const { mutateAsync: permitCoin } = useMutation({
    mutationFn: (request: PermitCoinRequest) => {
      console.log("호출");

      return PermitCoinAPI(request);
    },
  });

  return {
    bankList,
    permitCoin,
    send1won,
    verify1won,
    checkNickname,
    checkEmail,
    signup,
  };
};

export default useSignup;
