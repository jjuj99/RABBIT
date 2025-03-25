import AuctionBidList from "@/features/auction/ui/auctionBidList";
import AuctionBidPanel from "@/features/auction/ui/AuctionBidPanel";

const AuctionDetail = () => {
  return (
    <section>
      <div>
        <AuctionBidPanel CBP={123123} amount={123132} />
      </div>
      <div>
        <AuctionBidList></AuctionBidList>
      </div>
    </section>
  );
};

export default AuctionDetail;
