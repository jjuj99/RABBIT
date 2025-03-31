import { Circle } from "lucide-react";
import { BidHistoryResponse } from "@/features/auction/types/response";
import { formatDateToYMD, getBidStatusColor } from "@/shared/lib/utils";

interface AuctionBidHistoryDesktopProps {
  data: BidHistoryResponse[];
}

const AuctionBidHistoryDesktop = ({ data }: AuctionBidHistoryDesktopProps) => {
  return (
    <table className="w-full table-fixed">
      <thead>
        <tr className="border-b border-gray-800">
          <th className="w-[120px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            NFT
          </th>
          <th className="w-[140px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            경매 상태
          </th>
          <th className="w-[180px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            현재 가격
          </th>
          <th className="w-[180px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            내 입찰가
          </th>
          <th className="w-[100px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            상태
          </th>
          <th className="w-[160px] px-4 py-3 text-center text-xs font-medium text-gray-200 sm:text-base">
            거래일시
          </th>
        </tr>
      </thead>
      <tbody>
        {data.map((item) => (
          <tr key={item.auction_id} className="border-b border-gray-800">
            <td className="px-4 py-3">
              <div className="flex flex-col items-center gap-2">
                <div className="flex flex-wrap items-center justify-center">
                  <span className="font-bit text-xs font-medium text-white sm:text-base">
                    RABBIT
                  </span>
                  <span className="font-bit text-xs font-medium text-white sm:text-base">
                    #{item.auction_id}
                  </span>
                </div>
                <img
                  src="/images/NFT.png"
                  alt="NFT"
                  className="h-[100px] w-[100px] rounded-lg object-cover"
                />
              </div>
            </td>
            <td className="px-4 py-3 text-center">
              <div className="flex items-center justify-center gap-2 text-xs font-medium sm:text-base">
                {item.auction_status === "IN_PROGRESS" ? (
                  <>
                    <Circle
                      className="text-brand-primary fill-brand-primary"
                      width={8}
                      height={8}
                    />
                    <span>경매 진행중</span>
                  </>
                ) : (
                  <>
                    <Circle className="fill-gray-800" width={8} height={8} />
                    <span className="text-gray-200">경매 마감</span>
                  </>
                )}
              </div>
            </td>
            <td className="px-4 py-3 text-center text-xs text-white sm:text-base">
              {item.price.toLocaleString()} RAB
            </td>
            <td className="px-4 py-3 text-center">
              <div className="text-xs text-white sm:text-base">
                {item.bid_amount.toLocaleString()} RAB
              </div>
            </td>
            <td className="px-4 py-3 text-center">
              <div
                className={`mx-auto w-fit text-xs font-medium sm:text-base ${getBidStatusColor(item.bid_status)}`}
              >
                {item.bid_status === "WON"
                  ? "낙찰"
                  : item.bid_status === "LOST"
                    ? "유찰"
                    : "입찰중"}
              </div>
            </td>
            <td className="px-4 py-3 text-center text-xs text-white sm:text-base">
              {formatDateToYMD(item.bid_date)}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default AuctionBidHistoryDesktop;
