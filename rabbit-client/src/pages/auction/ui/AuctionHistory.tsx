import { getBidHistoryAPI } from "@/features/auction/api/auctionApi";
import AuctionBidHistory from "@/features/auction/ui/AuctionBIdHistory";
import { useQuery } from "@tanstack/react-query";

const AuctionHistory = () => {
  const { data: bidHistory, isLoading: bidHistoryLoading } = useQuery({
    queryKey: ["bidHistory"],
    queryFn: () => getBidHistoryAPI(),
  });

  if (bidHistoryLoading) {
    return <div className="py-4 text-center">로딩 중...</div>;
  }

  if (bidHistory?.error) {
    return (
      <div className="py-4 text-center text-red-500">
        {bidHistory.error.message}
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <h2 className="mb-6 text-xl font-bold sm:text-2xl">입찰 내역</h2>
      <AuctionBidHistory data={bidHistory?.data || []} />
    </div>
  );
};

export default AuctionHistory;
