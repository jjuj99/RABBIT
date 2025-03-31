import QuickIncreaseButtons from "@/entities/account/ui/QuickIncreaseButtons";
import Checkout from "@/entities/account/ui/toss/Checkout";
import { UnitInput } from "@/entities/common";
import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import { Button } from "@/shared/ui/button";
import { Dialog, DialogContent, DialogTrigger } from "@/shared/ui/dialog";
import currencyFormat from "@/shared/utils/currencyFormat";
import { DialogDescription, DialogTitle } from "@radix-ui/react-dialog";
import { useState } from "react";
import { toast } from "sonner";

const CoinChargeForm = ({
  amountState,
}: {
  amountState: [number, React.Dispatch<React.SetStateAction<number>>];
}) => {
  const [open, setOpen] = useState(false);
  const [amount, setAmount] = amountState;
  const { balance } = useGetBalance();

  return (
    <div className="flex flex-col justify-between gap-6 rounded-md bg-gray-900 p-6">
      <div className="flex items-end gap-2">
        <h3 className="text-xl md:text-2xl">충전하기</h3>
        <span className="text-xs text-gray-400 md:text-sm">(1 RAB ≒ 1 원)</span>
      </div>
      <div className="flex flex-col gap-3">
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
                    toast.error("충전 금액을 확인해주세요.");
                    return;
                  }
                }}
              >
                충전하기
              </Button>
            </DialogTrigger>
            <DialogContent className="bg-white">
              <DialogTitle className="text-2xl font-bold text-black">
                코인 충전
              </DialogTitle>
              <DialogDescription className="text-sm text-gray-600">
                실제 서비스 이용시 계좌이체, 가상계좌로 현금 입금만 가능하게
                제한할 예정이나,
                <br />
                개발 단계에서는 테스트 API의 제한으로 지금은 모든 결제 수단을
                제공합니다.
              </DialogDescription>
              <Checkout onClose={() => setOpen(false)} amount={amount} />
            </DialogContent>
          </Dialog>
        </div>
        <div className="flex flex-col justify-between rounded-md bg-gray-800 px-6 py-5 md:flex-row md:items-center">
          <span className="text-sm text-gray-400">충전 후 잔액 : </span>
          <span className="text-brand-primary text-base font-bold md:text-lg">
            {currencyFormat(amount + balance)}
            <span className="font-pixel pl-2">RAB</span>
          </span>
        </div>
      </div>
    </div>
  );
};

export default CoinChargeForm;
