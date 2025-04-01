import { InfoRow } from "@/entities/common/ui/InfoRow";
import LoanInfo from "@/features/loan/ui/LoanInfo";
import { Separator } from "@/shared/ui/Separator";
import { ProgressBar } from "@/entities/common/ui/ProgressBar";
import AuctionNFTEventList from "@/features/auction/ui/AuctionNFTEventList";

const BorrowDetail = () => {
  const startDate = "2024-04-30";
  const endDate = "2025-04-30";

  return (
    <div className="flex flex-col gap-8">
      <h2 className="text-2xl font-bold">채무 상세</h2>
      <div className="flex h-fit flex-row gap-4">
        <img src="/images/NFT.png" className="h-[466px] w-[466px] rounded-sm" />
        <div className="flex w-full flex-col gap-2">
          <LoanInfo />
          <div>
            <div className="flex h-fit w-full flex-col gap-2 rounded-sm bg-gray-800 px-6 py-4">
              <InfoRow label="다음 상환 일자" value="2025-04-30" />
              <InfoRow label="상환 금액" value="100,000₩" />
            </div>
            <Separator className="w-full" />
          </div>
        </div>
      </div>
      <div className="flex flex-col gap-4 rounded-sm bg-gray-800 p-6">
        <div className="flex items-center justify-between">
          <div className="flex flex-col gap-1">
            <span className="text-sm text-gray-400">계약일</span>
            <span className="text-xl font-bold text-white">{startDate}</span>
          </div>
          <div className="flex flex-col items-end gap-1">
            <span className="text-sm text-gray-400">계약일</span>
            <span className="text-xl font-bold text-white">{endDate}</span>
          </div>
        </div>
        <ProgressBar startDate={startDate} endDate={endDate} />
      </div>
      <h2 className="text-2xl font-bold">이벤트 리스트</h2>
      <AuctionNFTEventList />
    </div>
  );
};

export default BorrowDetail;
