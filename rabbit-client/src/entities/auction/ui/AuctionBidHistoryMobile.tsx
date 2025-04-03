import { BidHistoryResponse } from "@/features/auction/types/response";
import { Separator } from "@/shared/ui/Separator";
import { formatDateToYMD, getBidStatusColor } from "@/shared/lib/utils";

interface AuctionBidHistoryMobileProps {
  data: BidHistoryResponse[];
}

const AuctionBidHistoryMobile = ({ data }: AuctionBidHistoryMobileProps) => {
  return (
    <>
      {data.map((item) => (
        <div key={item.auctionId} className="mb-4 rounded-sm bg-gray-800 p-4">
          <div className="flex flex-row gap-4">
            <div className="flex w-fit flex-col gap-3">
              <div className="flex flex-col">
                <span className="font-bit text-xs font-medium text-white sm:text-base">
                  RABBIT
                </span>
                <span className="font-bit text-brand-primary text-xs font-medium sm:text-base">
                  #{item.auctionId}
                </span>
              </div>
              <div className="h-[64px] w-[64px] sm:h-[84px] sm:w-[84px]">
                <img
                  src="/images/NFT.png"
                  alt="NFT"
                  className="h-full w-full rounded-sm object-cover"
                />
              </div>
            </div>
            <div className="flex w-full flex-col gap-2">
              <div className="flex flex-row items-center justify-between">
                <span className="text-xs font-light text-white sm:text-base">
                  {formatDateToYMD(item.bidDate)}
                </span>
                <span
                  className={`text-xs font-medium sm:text-base ${getBidStatusColor(item.bidStatus)}`}
                >
                  {item.bidStatus === "WON"
                    ? "낙찰"
                    : item.bidStatus === "LOST"
                      ? "유찰"
                      : "입찰중"}
                </span>
              </div>
              <Separator className="w-full" />
              <div className="flex flex-col gap-1">
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    현재가격
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.price.toLocaleString()} RAB
                  </span>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    경매 상태
                  </span>
                  <div className="flex items-center gap-2 text-xs font-medium sm:text-base">
                    {item.auctionStatus === "IN_PROGRESS" ? (
                      <span>경매 진행중</span>
                    ) : (
                      <span>경매 마감</span>
                    )}
                  </div>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    내 입찰가
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.bidAmount.toLocaleString()} RAB
                  </span>
                </div>
                <div className="flex flex-row justify-between"></div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </>
  );
};

export default AuctionBidHistoryMobile;
