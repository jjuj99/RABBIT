import { MenubarSeparator } from "@radix-ui/react-menubar";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";

const AuctionBidList = () => {
  //mock data 입니다.
  const data = [
    {
      bid_id: 1,
      bid_amount: 10020,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 2,
      bid_amount: 15050,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 3,
      bid_amount: 20000,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 1,
      bid_amount: 10020,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 2,
      bid_amount: 15050,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 3,
      bid_amount: 20000,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 1,
      bid_amount: 10020,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 2,
      bid_amount: 15050,
      created_at: "2025-03-12T14:30:00",
    },
    {
      bid_id: 3,
      bid_amount: 20000,
      created_at: "2025-03-12T14:30:00",
    },
  ];

  const BidList = data.map((bid) => {
    const [date, time] = bid.created_at.split("T");
    return (
      <>
        <div
          key={bid.bid_id}
          className="flex justify-between rounded-sm bg-gray-800 px-4 py-2"
        >
          <div className="flex flex-row gap-2">
            <span className="text-lg font-medium text-gray-100">{date}</span>
            <span className="text-lg font-light text-gray-100">{time}</span>
          </div>
          <span className="text-lg font-medium text-white">
            {bid.bid_amount.toLocaleString()} RAB
          </span>
        </div>
        <MenubarSeparator className="h-[0.2px] bg-white" />
      </>
    );
  });

  return (
    <div className="w-[728px] rounded-sm bg-gray-900 px-6 py-6">
      <h1 className="mb-4 text-2xl font-medium">입찰 내역</h1>
      <ScrollArea className="h-[400px] w-full">
        <ScrollAreaViewport className="h-full w-full">
          <div className="flex flex-col gap-4">{BidList}</div>
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
