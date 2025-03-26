import { useCallback } from "react";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { MenubarSeparator } from "@/shared/ui/menubar";
import IRSection from "@/entities/auction/AuctionFilter/IRSection";
import REPAY_TYPESection from "@/entities/auction/AuctionFilter/REPAY_TYPESection";
import MAT_DTSection from "@/entities/auction/AuctionFilter/MAT_DTSection";
import PriceSection from "@/entities/auction/AuctionFilter/PriceSection";
import { useAuctionFilterErrorStore } from "@/shared/lib/store/auctionFilterErrorStore";
import { cn } from "@/shared/lib/utils";

interface AuctionFilterProps {
  className?: string;
}

const AuctionFilter = ({ className }: AuctionFilterProps) => {
  const triggerApi = useCallback(() => {
    // 전역 에러 스토어에서 모든 에러를 가져옴
    const { errors } = useAuctionFilterErrorStore.getState();
    const combinedError = Object.values(errors)
      .filter((e) => e !== "")
      .join(", ");

    if (combinedError) {
      console.error(combinedError);
      return;
    }

    const state = useAuctionFilterStore.getState();
    const query = `/api/auctions?min_price=${state.minPrice}&max_price=${state.maxPrice}&min_rate=${state.minIR}&max_rate=${state.maxIR}&type=${state.paymentTypes.join(
      ",",
    )}&maturity=${state.maturity}${
      state.maturity === "directSelect"
        ? `&start_date=${state.startDate}&end_date=${state.endDate}`
        : ""
    }`;
    console.log(query);
    // 실제 API 호출 로직 추가.....
  }, []);

  return (
    <div
      className={cn(
        "flex h-fit w-[278px] flex-col gap-5 rounded-sm border border-white bg-gray-900 px-5 py-6",
        className,
      )}
    >
      <PriceSection triggerApi={triggerApi} />
      <MenubarSeparator className="h-[0.2px] bg-white" />
      <IRSection triggerApi={triggerApi} />
      <MenubarSeparator className="h-[0.2px] bg-white" />
      <REPAY_TYPESection triggerApi={triggerApi} />
      <MenubarSeparator className="h-[0.2px] bg-white" />
      <MAT_DTSection triggerApi={triggerApi} />
    </div>
  );
};

export default AuctionFilter;
