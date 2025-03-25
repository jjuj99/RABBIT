import { NFTCard } from "@/entities/NFT/NFTcard";
import AuctionFilter from "@/features/auction/ui/AuctionFilter";

const AuctionList = () => {
  const size = 10;

  return (
    <div className="flex w-full flex-row gap-9">
      <AuctionFilter />
      <section className="flex flex-1 flex-col gap-9">
        <div className="w-full rounded-lg border border-white bg-gray-900 px-6 py-3">
          <h2 className="font-dunggeunmo text-2xl">
            경매 진행중인 차용증 목록 ({size})
          </h2>
        </div>

        <NFTCard />
      </section>
    </div>
  );
};

export default AuctionList;
