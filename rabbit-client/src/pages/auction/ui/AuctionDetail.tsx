import AuctionBidList from "@/features/auction/ui/AuctionBidList";
import AuctionBidPanel from "@/features/auction/ui/AuctionBidPanel";
import PNInfoList from "@/features/auction/ui/PNInfoList";

const AuctionDetail = () => {
  return (
    <section className="flex h-full flex-col gap-4">
      <h2 className="font-bit h-fit w-full flex-col gap-4 rounded-xl bg-gray-900 px-4 py-4 text-xl sm:text-3xl">
        "RABBIT #1"
      </h2>
      <div className="flex w-full flex-col gap-8">
        <div className="flex h-fit w-full flex-col items-center gap-6 lg:flex-row">
          {/* 왼쪽 영역 */}
          <div className="flex h-full flex-1 flex-col items-center gap-4">
            <img
              alt="NFT"
              className="h-auto w-full object-contain"
              src="/images/NFT.png"
            />
          </div>
          {/* 오른쪽 영역 */}
          <div className="flex h-full w-full flex-1 flex-col gap-8">
            <AuctionBidPanel CBP={123123} amount={123132} />
            <AuctionBidList />
          </div>
        </div>
        <div className="flex h-fit w-full items-center justify-center gap-4 rounded-sm bg-gray-900 py-4 sm:h-[82px]">
          <span className="font-medium sm:text-2xl">경매 종료까지</span>
          <span className="sm-font-bold text-2xl font-bold sm:text-4xl">
            10:10:10
          </span>
        </div>
        <div className="flex w-full justify-center">
          <PNInfoList />
        </div>
      </div>
    </section>
  );
};

export default AuctionDetail;
