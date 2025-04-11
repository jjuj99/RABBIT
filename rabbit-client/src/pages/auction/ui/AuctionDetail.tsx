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
import AuctionCountdownTimer from "@/shared/ui/AuctionCountdownTimer";

const AuctionDetail = () => {
  const { auctionId } = useParams<{ auctionId: string }>();
  const isDesktop = useMediaQuery("lg");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [latestBid, setLatestBid] = useState<BidUpdateEvent["data"] | null>(
    null,
  );

  const VITE_API_URL = import.meta.env.VITE_API_URL;
  const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

  useEffect(() => {
    if (!auctionId) return;

    const eventSource = new EventSource(
      `${VITE_API_URL}/${VITE_API_VERSION}/sse/subscribe/auction?id=${auctionId}`,
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
    isError: PNInfoError,
    refetch: refetchPNInfo,
  } = useQuery({
    queryKey: ["PNInfoList", auctionId],
    queryFn: () => getPNInfoListAPI(Number(auctionId)),
  });
  console.log(PNInfo);
  const [currentBidPrice, setCurrentBidPrice] = useState<number>(0);
  useEffect(() => {
    if (PNInfo?.data?.price) {
      setCurrentBidPrice(PNInfo.data.price);
    }
  }, [PNInfo]);
  const {
    data: bidList,
    isLoading: bidListLoading,
    isError: bidListError,
    refetch: refetchBidList,
  } = useQuery({
    queryKey: ["bidList", auctionId],
    queryFn: () => getBidListAPI(Number(auctionId)),
  });

  const {
    data: EventList,
    isLoading: EventListLoading,
    isError: EventListError,
  } = useQuery({
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

  if (!auctionId) return;

  return (
    <>
      <section className="flex h-full flex-col gap-4">
        <div className="flex items-center rounded-xl bg-gray-900 p-4">
          <h2 className="font-bit h-fit w-full flex-col gap-4 text-xl sm:text-3xl">
            {"RABBIT #" + PNInfo?.data?.tokenId}
          </h2>
          <Button
            variant="gradient"
            size="sm"
            onClick={() => {
              if (PNInfo?.data?.pdfUrl) {
                window.open(
                  PNInfo.data.pdfUrl,
                  "_blank",
                  "noopener,noreferrer",
                );
              }
            }}
          >
            계약서 보기
          </Button>
        </div>

        <div className="flex w-full flex-col gap-8">
          <div className="flex h-fit w-full flex-col items-center gap-6 lg:flex-row">
            {/* 왼쪽 영역 */}
            <div className="flex h-full flex-1 flex-col items-center gap-4">
              <img
                alt="NFT"
                className="h-auto w-full object-contain"
                src={PNInfo?.data?.nftImageUrl}
              />
            </div>
            {/* 오른쪽 영역 */}
            <div className="flex h-full w-full flex-1 flex-col gap-4">
              {/* PN 정보 패널: 에러와 로딩 별도 처리 */}
              {PNInfoLoading ? (
                <div className="loader-sprite" />
              ) : PNInfoError ? (
                <div>PN 정보 로드 중 에러가 발생했습니다.</div>
              ) : (
                <AuctionBidPanel CBP={currentBidPrice} />
              )}

              {/* 입찰 목록: 에러와 로딩 별도 처리 */}
              {bidListLoading ? (
                <div className="loader-sprite" />
              ) : bidListError ? (
                <div>입찰 목록 로드 중 에러가 발생했습니다.</div>
              ) : (
                <AuctionBidList data={bidList?.data || []} />
              )}
            </div>
          </div>

          <div className="flex h-fit w-full items-center justify-center gap-4 rounded-sm bg-gray-900 py-4 sm:h-[82px]">
            {PNInfo?.data?.endDate &&
              new Date(PNInfo.data.endDate) > new Date() && (
                <span className="font-medium sm:text-2xl">경매 종료까지</span>
              )}
            <span className="sm-font-bold w-[100px] text-2xl font-bold sm:text-4xl">
              {PNInfo?.data ? (
                <AuctionCountdownTimer
                  endDate={PNInfo.data.endDate}
                  status={PNInfo.data.auctionStatus}
                  mineFlag={PNInfo.data.mineFlag ?? false}
                  auctionId={auctionId!}
                />
              ) : (
                <div className="loader-sprite" />
              )}
            </span>
          </div>

          <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4 sm:pt-4 sm:pb-6">
            <h3 className="rounded-sm px-3 py-2 text-lg font-semibold text-white sm:text-xl">
              차용증 정보
            </h3>
            <div className="flex w-full justify-center">
              {PNInfoLoading ? (
                <div className="loader-sprite" />
              ) : PNInfoError ? (
                <div>PN 정보 에러.</div>
              ) : PNInfo?.data ? (
                <PNInfoList data={PNInfo.data} />
              ) : (
                <div>데이터가 없습니다.</div>
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
              {EventListLoading ? (
                <div>로딩중...</div>
              ) : EventListError ? (
                <div>이벤트 기록 로드 에러.</div>
              ) : (
                EventList?.data &&
                (isDesktop ? (
                  <NFTEventList data={EventList.data.eventList} />
                ) : (
                  <NFTEventListMobile data={EventList.data.eventList} />
                ))
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
