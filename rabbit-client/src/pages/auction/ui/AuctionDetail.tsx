import {
  getAuctionSimilarListAPI,
  getBidListAPI,
  getNFTEventListAPI,
  getPNInfoListAPI,
} from "@/features/auction/api/auctionApi";
import AuctionBidList from "@/entities/auction/ui/AuctionBidList";
import AuctionBidPanel from "@/entities/auction/ui/AuctionBidPanel";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import PNInfoList from "@/entities/auction/ui/PNInfoList";
import CountdownTimer from "@/shared/ui/CountdownTimer";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import NFTEventListMobile from "@/entities/common/ui/NFTEventListMobile";
import NFTEventList from "@/entities/common/ui/NFTEventList";
import AuctionSimilarList from "@/entities/auction/ui/AuctionSimilarList";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/shared/ui/dialog";
import { Button } from "@/shared/ui/button";
import { BidUpdateEvent } from "@/entities/auction/types/NFTEventType";

const AuctionDetail = () => {
  const { auctionId } = useParams<{ auctionId: string }>();
  const isDesktop = useMediaQuery("lg");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [latestBid, setLatestBid] = useState<BidUpdateEvent["data"] | null>(
    null,
  );

  useEffect(() => {
    if (!auctionId) return;

    const eventSource = new EventSource(
      `/subscribe?type=auction&id=${auctionId}`,
    );

    eventSource.onmessage = (event) => {
      try {
        const bidUpdate = JSON.parse(event.data) as BidUpdateEvent;
        if (bidUpdate.event === "bid-updated") {
          setLatestBid(bidUpdate.data);
          setIsDialogOpen(true);
        }
      } catch (error) {
        console.error("SSE 데이터 파싱 오류:", error);
      }
    };

    eventSource.onerror = (error) => {
      console.error("SSE 연결 에러:", error);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [auctionId]);

  const {
    data: PNInfo,
    isLoading: PNInfoLoading,
    refetch: refetchPNInfo,
  } = useQuery({
    queryKey: ["PNInfoList", auctionId],
    queryFn: () => getPNInfoListAPI(Number(auctionId)),
  });

  const {
    data: bidList,
    isLoading: bidListLoading,
    refetch: refetchBidList,
  } = useQuery({
    queryKey: ["bidList", auctionId],
    queryFn: () => getBidListAPI(Number(auctionId)),
  });

  const { data: EventList, isLoading: EventListLoading } = useQuery({
    queryKey: ["NFTEventList", auctionId],
    queryFn: () => getNFTEventListAPI(Number(auctionId)),
  });

  const { data: AuctionSimilarListdata } = useQuery({
    queryKey: ["AuctionSimilarList", auctionId],
    queryFn: () => getAuctionSimilarListAPI(Number(auctionId)),
  });

  const handleRefresh = () => {
    refetchPNInfo();
    refetchBidList();
    setIsDialogOpen(false);
  };

  if (EventListLoading) {
    return <div>로딩중...</div>;
  }

  if (!EventList?.data) {
    return <div>데이터가 없습니다.</div>;
  }

  if (PNInfoLoading || bidListLoading) {
    return <div>로딩중...</div>;
  }

  if (!PNInfo?.data || !bidList?.data) {
    return <div>데이터가 없습니다.</div>;
  }

  return (
    <>
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
              <AuctionBidPanel CBP={PNInfo?.data?.price} />

              <AuctionBidList data={bidList?.data || []} />
            </div>
          </div>

          <div className="flex h-fit w-full items-center justify-center gap-4 rounded-sm bg-gray-900 py-4 sm:h-[82px]">
            <span className="font-medium sm:text-2xl">경매 종료까지</span>
            <span className="sm-font-bold w-[100px] text-2xl font-bold sm:text-4xl">
              <CountdownTimer endDate={PNInfo.data.endDate} />
            </span>
          </div>

          <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4 sm:pt-4 sm:pb-6">
            <h3 className="rounded-sm px-3 py-2 text-lg font-semibold text-white sm:text-xl">
              차용증 정보
            </h3>
            <div className="flex w-full justify-center">
              {PNInfoLoading ? (
                <div>로딩중...</div>
              ) : (
                PNInfo?.data && <PNInfoList data={PNInfo.data} />
              )}
            </div>
            <div className="w-full">
              {AuctionSimilarListdata?.data && (
                <AuctionSimilarList data={AuctionSimilarListdata?.data} />
              )}
            </div>
          </div>
          <div className="flex flex-col gap-1 sm:rounded-lg sm:bg-gray-900 sm:p-4">
            <h3 className="bg-succecss px-3 py-2 text-lg font-semibold text-white sm:py-2 sm:text-xl">
              차용증 기록
            </h3>
            <div className="w-full rounded-sm bg-gray-900">
              {isDesktop ? (
                <NFTEventList data={EventList.data.eventList} />
              ) : (
                <NFTEventListMobile data={EventList.data.eventList} />
              )}
            </div>
          </div>
        </div>
      </section>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>새로운 입찰 알림</DialogTitle>
            <DialogDescription>새로운 입찰이 발생했습니다.</DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              <p className="text-sm text-gray-400">입찰 금액</p>
              <p className="text-lg font-semibold">
                {latestBid?.bidAmount.toLocaleString()} 원
              </p>
            </div>
            <div className="flex flex-col gap-2">
              <p className="text-sm text-gray-400">입찰 시간</p>
              <p className="text-lg font-semibold">{latestBid?.createdAt}</p>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                닫기
              </Button>
              <Button onClick={handleRefresh}>새로고침</Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default AuctionDetail;
