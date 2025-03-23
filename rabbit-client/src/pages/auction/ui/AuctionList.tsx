import { NFTCard } from "@/entities/NFT/NFTcard";
import AuctionFilter from "@/features/auction/ui/AuctionFilter";

const AuctionList = () => {
  return (
    <div className="flex gap-9">
      <AuctionFilter />
      <NFTCard />
    </div>
  );
};

export default AuctionList;
