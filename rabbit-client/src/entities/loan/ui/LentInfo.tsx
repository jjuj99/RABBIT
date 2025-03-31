import { Circle, Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { LentInfoResponse } from "../types/response";

interface LentInfoProps {
  data?: LentInfoResponse[];
}

const LentInfo = ({ data = [] }: LentInfoProps) => {
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
    <div>
      <table className="w-full table-fixed rounded-sm bg-gray-900">
        <thead>
          <tr className="border-b border-gray-600">
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              NFT
            </th>
            <th className="w-[70px] px-4 py-3 text-center text-base font-medium text-gray-200">
              채무자
            </th>
            <th className="w-[180px] px-4 py-3 text-center text-base font-medium text-gray-200">
              지갑 주소
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              금액
            </th>
            <th className="w-[70px] px-4 py-3 text-center text-base font-medium text-gray-200">
              수익률
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              만기일
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              상태
            </th>
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.tokenId} className="border-b border-gray-800">
              <td className="px-4 py-3">
                <div className="flex flex-col items-center gap-2">
                  <div className="flex flex-wrap items-center justify-center">
                    <span className="font-bit text-base font-medium text-white">
                      RABBIT
                    </span>
                    <span className="font-bit text-base font-medium text-white">
                      #{item.tokenId}
                    </span>
                  </div>
                  <img
                    src={item.tokenImage}
                    alt="NFT"
                    className="h-[100px] w-[100px] rounded-lg object-cover"
                  />
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="flex items-center justify-center gap-2 text-base font-medium">
                  {item.debtorName}
                </div>
              </td>
              <td className="px-4 py-3 text-center text-base text-white">
                <div
                  className="hover:text-brand-primary mx-auto max-w-[180px] cursor-pointer truncate"
                  onClick={() => {
                    setSelectedWallet(item.debtorWallet);
                    setIsOpen(true);
                  }}
                >
                  {item.debtorWallet || "-"}
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="text-base text-white">
                  {item.loanAmount.toLocaleString()}₩
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="mx-auto w-fit text-base text-white">
                  {item.returnRate}%
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="flex flex-col gap-1">
                  <div className="text-base text-white">{item.endDate}</div>
                  <div className="text-sm text-gray-300">
                    {item.endDays}일 남음
                  </div>
                </div>
              </td>
              <td className="px-4 py-3">
                <div className="flex flex-col items-center justify-center gap-1">
                  <div className="flex items-center justify-center gap-1">
                    <Circle
                      className={
                        item.isOverdue
                          ? "fill-fail text-fail"
                          : "text-brand-primary fill-brand-primary"
                      }
                      width={8}
                      height={8}
                    />
                    <div>{item.isOverdue ? "연체" : "정상"}</div>
                  </div>
                  <div className="flex flex-wrap gap-1 text-sm text-gray-200">
                    <span className="text-gray-300">다음 납부일 </span>
                    <span>{item.nextDueDate} </span>
                  </div>
                  {item.isOverdue && item.overDueAmount && item.overdueDays && (
                    <div className="text-sm text-red-500">
                      연체금액: {item.overDueAmount.toLocaleString()}₩
                      <br />
                      연체일수: {item.overdueDays}일
                    </div>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

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
    </div>
  );
};

export default LentInfo;
