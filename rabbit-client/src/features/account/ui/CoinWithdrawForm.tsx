import useWithdraw from "@/entities/account/hook/useWithdraw";
import QuickIncreaseButtons from "@/entities/account/ui/QuickIncreaseButtons";
import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { UnitInput } from "@/entities/common";
import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import { Button } from "@/shared/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogTitle,
  DialogTrigger,
} from "@/shared/ui/dialog";
import currencyFormat from "@/shared/utils/currencyFormat";
import { useEffect, useState } from "react";
import { toast } from "sonner";

const CoinWithdrawForm = ({
  amountState,
}: {
  amountState: [number, React.Dispatch<React.SetStateAction<number>>];
}) => {
  const [amount, setAmount] = amountState;
  const [open, setOpen] = useState(false);
  const { balance } = useGetBalance();
  const { user } = useAuthUser();
  const { withdraw } = useWithdraw();
  useEffect(() => {
    if (amount > balance) {
      toast.error("출금 금액이 잔액을 초과합니다.");
      setAmount(0);
    }
  }, [amount, balance]);

  const handleWithdraw = async () => {
    if (!user) {
      toast.error("로그인 후 이용해주세요.");
      return;
    }
    await withdraw({
      name: user?.userName,
      accountNumber: user?.refundAccount,
      amount,
    });

    setOpen(false);
    setAmount(0);
  };
  return (
    <div className="flex flex-col gap-6 rounded-md bg-gray-900 p-6">
      <div className="flex items-end gap-2">
        <h3 className="text-xl md:text-2xl">출금하기</h3>
        <span className="text-xs text-gray-400 md:text-sm">(1 RAB ≒ 1 원)</span>
      </div>
      <div className="flex flex-col gap-3">
        <div className="flex flex-col gap-2">
          <div className="flex justify-between gap-2 py-2">
            <div className="flex flex-1 justify-between gap-2 rounded-md bg-gray-600 px-4 py-2">
              <span>예금주</span>

              <span>{user?.userName}</span>
            </div>
            <div className="flex flex-1 justify-between gap-2 rounded-md bg-gray-600 px-4 py-2">
              <span>은행명</span>
              <span>{user?.bankName}</span>
            </div>
          </div>
          <div className="flex flex-1 justify-between gap-2 rounded-md bg-gray-600 px-4 py-2">
            <span>계좌번호</span>
            <span>{user?.refundAccount}</span>
          </div>
        </div>
        <QuickIncreaseButtons setState={setAmount} />

        <div className="flex gap-3">
          <UnitInput
            type="number"
            unit="RAB"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            wrapperClassName="w-[70%]"
            className="w-full text-base font-bold md:text-lg"
            unitColor="brand-primary"
          />
          <Dialog
            open={open}
            onOpenChange={(isOpen) => {
              if (open && !isOpen) {
                return;
              }
              setOpen(isOpen);
            }}
            modal
          >
            <DialogTrigger asChild>
              <Button
                type="button"
                variant="primary"
                className="w-[30%]"
                onClick={(e) => {
                  if (amount <= 0) {
                    e.preventDefault(); // 모달 열림 방지
                    toast.error("출금 금액을 확인해주세요.");
                    return;
                  }
                }}
              >
                출금하기
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogTitle className="text-2xl font-bold">
                코인 출금
              </DialogTitle>
              <DialogDescription className="text-base">
                {`${user?.userName}님의 계좌로 ${currencyFormat(amount)}원을 출금하시겠습니까?`}
              </DialogDescription>
              <DialogFooter>
                <Button
                  variant="secondary"
                  type="button"
                  onClick={() => setOpen(false)}
                >
                  취소
                </Button>
                <Button
                  onClick={handleWithdraw}
                  variant="primary"
                  type="button"
                >
                  출금
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
        <div className="flex flex-col justify-between rounded-md bg-gray-800 px-6 py-5 md:flex-row md:items-center">
          <span className="text-sm text-gray-400">출금 후 잔액 : </span>
          <span className="text-brand-primary text-base font-bold md:text-lg">
            {currencyFormat(balance - amount)}
            <span className="font-pixel pl-2">RAB</span>
          </span>
        </div>
      </div>
    </div>
  );
};

export default CoinWithdrawForm;
