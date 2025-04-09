import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { BidListResponse } from "@/features/auction/types/response";
import { Separator } from "@/shared/ui/Separator";

interface AuctionBidList {
  data: BidListResponse[];
}

const AuctionBidList = ({ data }: AuctionBidList) => {
  if (data.length === 0) {
    return (
      <div className="w-full rounded-sm bg-gray-900 px-4 py-4 sm:px-6">
        <h2 className="mb-4 text-lg font-semibold sm:text-2xl">입찰 내역</h2>
        <div className="flex h-[100px] w-full items-center justify-center text-gray-100 lg:h-[316px]">
          입찰 내역이 없습니다
        </div>
      </div>
    );
  }

  const BidList = (
    <ul className="flex flex-col">
      {data.map((bid, index) => {
        const [date, time] = bid.createdAt.split("T");
        return (
          <li key={bid.bidId} className="flex flex-col">
            <div className="flex w-full flex-col justify-between gap-2 rounded-sm bg-gray-800 px-4 py-2 sm:flex-row sm:gap-4">
              <div className="flex flex-row gap-1 sm:gap-2">
                <span className="text-sm font-medium text-gray-100 sm:text-lg">
                  {date}
                </span>
                <span className="text-sm font-light text-gray-100 sm:text-lg">
                  {time}
                </span>
              </div>
              <span className="text-lg font-medium text-white">
                {bid.bidAmount.toLocaleString()} RAB
              </span>
            </div>
            {index < data.length - 1 && (
              <div className="my-4">
                <Separator />
              </div>
            )}
          </li>
        );
      })}
    </ul>
  );

  return (
    <div className="w-full rounded-sm bg-gray-900 px-4 py-4 sm:px-6">
      <h2 className="mb-4 text-lg font-semibold sm:text-2xl">입찰 내역</h2>
      <ScrollArea className="h-[316px] w-full">
        <ScrollAreaViewport className="h-full w-full">
          {BidList}
        </ScrollAreaViewport>
        <ScrollAreaScrollbar
          orientation="vertical"
          className="w-1 bg-gray-600 opacity-100"
        >
          <ScrollAreaThumb className="rounded-sm bg-white" />
        </ScrollAreaScrollbar>
      </ScrollArea>
    </div>
  );
};

export default AuctionBidList;
