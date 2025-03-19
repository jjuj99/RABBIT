import { NFTCard } from "@/entities/NFT/NFTcard";
import AuctionFilter from "./Create/AuctionFilter";

const AuctionList = () => {
  return (
    <div className="flex gap-4">
      <AuctionFilter />
      <NFTCard />
    </div>
  );
};

export default AuctionList;
