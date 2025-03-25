import AuctionBidList from "@/features/auction/ui/AuctionBidList";
import AuctionBidPanel from "@/features/auction/ui/AuctionBidPanel";
import AuctionNFTEventList from "@/features/auction/ui/AuctionNFTEventList";
import PNInfoList from "@/features/auction/ui/PNInfoList";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { useEffect, useState } from "react";

function CountdownTimer() {
  const [time, setTime] = useState("10:10:10"); // 초기 시간

  useEffect(() => {
    const timer = setInterval(() => {
      setTime((prevTime) => {
        const [h, m, s] = prevTime.split(":").map(Number);
        let totalSeconds = h * 3600 + m * 60 + s;

        if (totalSeconds <= 0) {
          clearInterval(timer);
          return "00:00:00";
        }

        totalSeconds -= 1;
        const newH = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
        const newM = String(Math.floor((totalSeconds % 3600) / 60)).padStart(
          2,
          "0",
        );
        const newS = String(totalSeconds % 60).padStart(2, "0");

        return `${newH}:${newM}:${newS}`;
      });
    }, 1000);

    return () => clearInterval(timer); // 컴포넌트 언마운트 시 정리
  }, []);

  return (
    <span className="sm-font-bold text-2xl font-bold sm:text-4xl">{time}</span>
  );
}

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
          <div className="flex h-full w-full flex-1 flex-col gap-4">
            <AuctionBidPanel CBP={123123} amount={123132} />
            <AuctionBidList />
          </div>
        </div>

        <div className="flex h-fit w-full items-center justify-center gap-4 rounded-sm bg-gray-600 py-4 sm:h-[82px]">
          <span className="font-medium sm:text-2xl">경매 종료까지</span>
          <span className="sm-font-bold w-[100px] text-2xl font-bold sm:text-4xl">
            {CountdownTimer()}
          </span>
        </div>
        <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4 sm:pt-4 sm:pb-6">
          <h3 className="rounded-sm px-3 py-2 text-lg font-semibold text-white sm:text-2xl">
            차용증 정보
          </h3>
          <div className="flex w-full justify-center">
            <PNInfoList />
          </div>
        </div>
        <div className="flex flex-col gap-4 rounded-lg sm:bg-gray-900 sm:p-4">
          <h3 className="bg-succecss px-3 text-lg font-semibold text-white sm:py-2 sm:text-2xl">
            차용증 기록
          </h3>
          <ScrollArea className="h-[300px] w-full">
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
