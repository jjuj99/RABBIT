import { Circle, Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { Separator } from "@/shared/ui/Separator";
import { LentInfoResponse } from "../types/response";

interface LentInfoMobileProps {
  data?: LentInfoResponse[];
}

const LentInfoMobile = ({ data = [] }: LentInfoMobileProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [selectedWallet, setSelectedWallet] = useState("");

  const handleCopy = async (walletAddress: string) => {
    try {
      await navigator.clipboard.writeText(walletAddress);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    } catch (err) {
      console.error("복사 실패:", err);
    }
  };

  if (data.length === 0) {
    return (
      <div className="flex h-[200px] items-center justify-center rounded-sm bg-gray-900">
        <div className="text-base text-gray-400">대출 정보가 없습니다.</div>
      </div>
    );
  }

  return (
    <>
      {data.map((item) => (
        <div key={item.tokenId} className="mb-4 rounded-sm bg-gray-800 p-4">
          <div className="flex flex-row gap-4">
            <div className="flex w-fit flex-col gap-3">
              <div className="flex flex-col">
                <span className="font-bit text-xs font-medium text-white sm:text-base">
                  RABBIT
                </span>
                <span className="font-bit text-brand-primary text-xs font-medium sm:text-base">
                  #{item.tokenId}
                </span>
              </div>
              <div className="h-[64px] w-[64px] sm:h-[84px] sm:w-[84px]">
                <img
                  src={item.tokenImage}
                  alt="NFT"
                  className="h-full w-full rounded-sm object-cover"
                />
              </div>
            </div>
            <div className="flex w-full flex-col gap-2">
              <div className="flex flex-row items-center justify-between">
                <span className="text-xs font-light text-white sm:text-base">
                  만기일 {item.endDate}
                </span>
                <div className="flex items-center gap-2">
                  <Circle
                    className={
                      item.isOverdue
                        ? "fill-fail text-fail"
                        : "text-brand-primary fill-brand-primary"
                    }
                    width={8}
                    height={8}
                  />
                  <span className="text-xs font-medium sm:text-base">
                    {item.isOverdue ? "연체" : "정상"}
                  </span>
                </div>
              </div>
              <Separator className="w-full" />
              <div className="flex flex-col gap-1">
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    채무자
                  </span>
                  <div className="flex flex-col items-end">
                    <span className="text-xs font-medium text-white sm:text-base">
                      {item.debtorName}
                    </span>
                    <span
                      className="hover:text-brand-primary cursor-pointer text-xs font-light text-gray-200 sm:text-sm"
                      onClick={() => {
                        setSelectedWallet(item.debtorWallet);
                        setIsOpen(true);
                      }}
                    >
                      {item.debtorWallet
                        ? `${item.debtorWallet.slice(0, 6)}...${item.debtorWallet.slice(-4)}`
                        : "-"}
                    </span>
                  </div>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    대출금액
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.loanAmount.toLocaleString()}₩
                  </span>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    수익률
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.returnRate}%
                  </span>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    다음 납부일
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.nextDueDate}
                  </span>
                </div>
                {item.isOverdue && item.overDueAmount && item.overdueDays && (
                  <div className="flex flex-row justify-between">
                    <span className="text-xs font-light text-red-500 sm:text-base">
                      연체금액
                    </span>
                    <span className="text-xs font-medium text-red-500 sm:text-base">
                      {item.overDueAmount.toLocaleString()}₩
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      ))}

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="bg-gray-900 text-white">
          <DialogTitle className="sr-only">지갑 주소 복사</DialogTitle>
          <div className="flex flex-col gap-4">
            <div className="text-lg font-medium">지갑 주소</div>
            <div className="flex items-center gap-2 rounded-lg bg-gray-800 p-3">
              <div className="flex-1 break-all">{selectedWallet}</div>
              <button
                onClick={() => handleCopy(selectedWallet)}
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
    </>
  );
};

export default LentInfoMobile;
