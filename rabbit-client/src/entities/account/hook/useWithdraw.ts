import { useMutation } from "@tanstack/react-query";
import { WithdrawAPI } from "../api/accountApi";
import { toast } from "sonner";

const useWithdraw = () => {
  const { mutateAsync: withdraw } = useMutation({
    mutationFn: WithdrawAPI,
    onSuccess: () => {
      toast.success("출금 완료");
    },
    onError: () => {
      toast.error("출금 실패");
    },
  });

  return { withdraw };
};

export default useWithdraw;
