import { BidHistoryResponse } from "@/features/auction/types/response";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import AuctionBidHistoryDesktop from "../../../entities/auction/ui/AuctionBidHistoryDesktop";
import AuctionBidHistoryMobile from "../../../entities/auction/ui/AuctionBidHistoryMobile";

interface AuctionBidHistoryProps {
  data?: BidHistoryResponse[];
}

const AuctionBidHistory = ({ data }: AuctionBidHistoryProps) => {
  const isDesktop = useMediaQuery("lg");

  if (!data || data.length === 0) {
    return (
      <div className="w-full overflow-hidden rounded-lg bg-gray-900 p-4">
        <div className="text-center text-base text-gray-400">
          입찰 내역이 없습니다.
        </div>
      </div>
    );
  }

  return (
    <div className="w-full overflow-hidden rounded-lg bg-gray-900">
      {isDesktop ? (
        <AuctionBidHistoryDesktop data={data} />
      ) : (
        <AuctionBidHistoryMobile data={data} />
      )}
    </div>
  );
};

export default AuctionBidHistory;
