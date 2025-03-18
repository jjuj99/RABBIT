import { NFTCard } from "@/entities/NFT/NFTcard";
import { Input } from "@/shared/ui/input";

const AuctionList = () => {
  return (
    <div>
      <Input
        placeholder="입력값을 입력해보세요."
        // unit="원"
        borderType="none"
        className="text-right"
      />
      <NFTCard />
    </div>
  );
};

export default AuctionList;
