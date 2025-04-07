import { useState } from "react";
import { Separator } from "@/shared/ui/Separator";
import { Circle, Copy, Check } from "lucide-react";
import { InfoRow } from "@/entities/common/ui/InfoRow";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { pnStatus } from "@/shared/type/Types";

interface LoanInfoProps {
  tokenId: string;
  crName: string;
  crWallet: string;
  la: number;
  totalAmount: number;
  repayType: string;
  ir: number;
  dir: number;
  defCnt: number;
  contractDt: string;
  pnStatus: pnStatus;
  earlypayFlag?: boolean;
  earlypayFee?: number;
}

const LoanInfo = ({
  tokenId,
  crName,
  crWallet,
  la,
  totalAmount,
  repayType,
  ir,
  dir,
  defCnt,
  contractDt,
  pnStatus,
  earlypayFlag,
  earlypayFee,
}: LoanInfoProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const walletAddress = crWallet;

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(walletAddress);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    } catch (err) {
      console.error("복사 실패:", err);
    }
  };

  return (
    <div className="h-fit w-full rounded-sm bg-gray-800 p-6">
      <div className="flex w-full flex-col gap-2">
        <div className="flex flex-row items-center justify-between">
          <span className="font-bit text-sm font-medium text-white sm:text-base">
            RABBIT#{tokenId}
          </span>
          <div className="flex items-center gap-2">
            <Circle
              className="text-brand-primary fill-brand-primary"
              width={8}
              height={8}
            />
            <span className="text-sm font-medium sm:text-base">{pnStatus}</span>
          </div>
        </div>
        <Separator className="w-full" />
        <div className="flex flex-col gap-2">
          <InfoRow label="계약일" value={contractDt} />
          <InfoRow label="채권자" value={crName} />
          <InfoRow
            label="지갑 주소"
            value={
              <div
                className="hover:text-brand-primary max-w-[180px] cursor-pointer truncate"
                onClick={() => setIsOpen(true)}
              >
                {walletAddress}
              </div>
            }
          />
          <InfoRow label="대출 금액" value={`${la.toLocaleString()}₩`} />
          <InfoRow
            label="만기시 총 수취액"
            value={`${totalAmount.toLocaleString()}₩`}
          />
          <InfoRow label="상환 방식" value={repayType} />
          <InfoRow label="이자율" value={`${ir}%`} />
          <InfoRow label="연체 이자율" value={`${dir}%`} />
          <InfoRow label="연체" value={`${defCnt}회`} />
          {earlypayFee && earlypayFlag !== undefined && (
            <InfoRow label="기한 이익 상실" value={`${earlypayFee}%`} />
          )}
        </div>
      </div>

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="bg-gray-900 text-white">
          <DialogTitle className="sr-only">지갑 주소 복사</DialogTitle>
          <div className="flex flex-col gap-4">
            <div className="text-lg font-medium">지갑 주소</div>
            <div className="flex items-center gap-2 rounded-lg bg-gray-800 p-3">
              <div className="flex-1 break-all">{walletAddress}</div>
              <button
                onClick={handleCopy}
                className="flex items-center gap-1 rounded-lg bg-gray-700 px-3 py-1 hover:bg-gray-600"
              >
                {isCopied ? (
                  <Check className="text-brand-primary" size={16} />
                ) : (
                  <Copy size={16} />
                )}
                <span>{isCopied ? "복사됨" : "복사"}</span>
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default LoanInfo;
