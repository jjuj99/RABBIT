import UnitInput from "@/entities/common/ui/UnitInput";
import { NFTCard } from "@/entities/NFT/NFTcard";

const AuctionList = () => {
  return (
    <div>
      <UnitInput
        type="number"
        placeholder="입력값을 입력해보세요."
        unit="값"
        borderType="none"
        className="w-full"
        disabled={true}
        label="하이요"
      />
      <NFTCard />
    </div>
  );
};

export default AuctionList;
