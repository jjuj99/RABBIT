import {
  getBidListAPI,
  getPNInfoListAPI,
} from "@/features/auction/api/auctionApi";
import AuctionBidList from "@/entities/auction/ui/AuctionBidList";
import AuctionNFTEventList from "@/features/auction/ui/AuctionNFTEventList";
import AuctionBidPanel from "@/entities/auction/ui/AuctionBidPanel";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import PNInfoList from "@/entities/auction/ui/PNInfoList";
import CountdownTimer from "@/shared/ui/CountdownTimer";

const AuctionDetail = () => {
  const { auctionId } = useParams<{ auctionId: string }>();

  const { data: PNInfo, isLoading: PNInfoLoading } = useQuery({
    queryKey: ["PNInfoList", auctionId],
    queryFn: () => getPNInfoListAPI(Number(auctionId)),
  });

  const { data: bidList, isLoading: bidListLoading } = useQuery({
    queryKey: ["bidList", auctionId],
    queryFn: () => getBidListAPI(Number(auctionId)),
  });

  if (PNInfoLoading || bidListLoading) {
    return <div>로딩중...</div>;
  }

  if (!PNInfo?.data || !bidList?.data) {
    return <div>데이터가 없습니다.</div>;
  }

  return (
    <section className="flex h-full flex-col gap-4">
      <h2 className="font-bit h-fit w-full flex-col gap-4 rounded-xl bg-gray-900 px-4 py-4 text-xl sm:text-3xl">
        {"RABBIT #" + auctionId}
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
          <div className="flex h-full w-full flex-1 flex-col gap-4">
            <AuctionBidPanel CBP={PNInfo?.data?.price} amount={123132} />

            <AuctionBidList data={bidList?.data || []} />
          </div>
        </div>

        <div className="flex h-fit w-full items-center justify-center gap-4 rounded-sm bg-gray-600 py-4 sm:h-[82px]">
          <span className="font-medium sm:text-2xl">경매 종료까지</span>
          <span className="sm-font-bold w-[100px] text-2xl font-bold sm:text-4xl">
            <CountdownTimer endDate={PNInfo.data.end_date} />
          </span>
        </div>
        <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4 sm:pt-4 sm:pb-6">
          <h3 className="rounded-sm px-3 py-2 text-lg font-semibold text-white sm:text-2xl">
            차용증 정보
          </h3>
          <div className="flex w-full justify-center">
            {PNInfoLoading ? (
              <div>로딩중...</div>
            ) : (
              PNInfo?.data && <PNInfoList data={PNInfo.data} />
            )}
          </div>
        </div>
        <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4">
          <h3 className="bg-succecss px-3 text-lg font-semibold text-white sm:py-2 sm:text-2xl">
            차용증 기록
          </h3>
          <ScrollArea className="h-[300px] w-full md:px-4">
            <ScrollAreaViewport className="h-full w-full">
              <AuctionNFTEventList />
            </ScrollAreaViewport>
            {/* 수직 스크롤바*/}
            <ScrollAreaScrollbar
              orientation="vertical"
              className="w-1 bg-gray-600 py-1 opacity-100"
            >
              <ScrollAreaThumb className="rounded-sm bg-white" />
            </ScrollAreaScrollbar>
            {/* 수평 스크롤바*/}
            <ScrollAreaScrollbar
              orientation="horizontal"
              className="h-2 bg-gray-600 py-1 opacity-100"
            >
              <ScrollAreaThumb className="rounded-sm bg-white" />
            </ScrollAreaScrollbar>
          </ScrollArea>
        </div>
      </div>
    </section>
  );
};

export default AuctionDetail;
