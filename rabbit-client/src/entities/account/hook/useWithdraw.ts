import { useMutation } from "@tanstack/react-query";
import { WithdrawAPI } from "../api/accountApi";
import { toast } from "sonner";

const useWithdraw = () => {
  const { mutateAsync: withdraw, isPending } = useMutation({
    mutationFn: WithdrawAPI,
    onSuccess: () => {
      toast.success("출금 완료");
      // 데이터 리프레시 (캐시 무효화)

      // 페이지 새로고침
      window.location.reload();
    },
    onError: () => {
      toast.error("출금 실패");
    },
  });

  return { withdraw, isPending };
};

export default useWithdraw;
