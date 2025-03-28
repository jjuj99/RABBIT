import { useCallback } from "react";
import IRSection from "@/entities/auction/ui/IRSection";
import REPAY_TYPESection from "@/entities/auction/ui/REPAY_TYPESection";
import MAT_DTSection from "@/entities/auction/ui/MAT_DTSection";
import PriceSection from "@/entities/auction/ui/PriceSection";
import { useAuctionFilterErrorStore } from "@/shared/lib/store/auctionFilterErrorStore";
import { cn } from "@/shared/lib/utils";
import { Separator } from "@/shared/ui/Separator";

interface AuctionFilterProps {
  className?: string;
  onFilterChange: () => void;
}

const AuctionFilter = ({ className, onFilterChange }: AuctionFilterProps) => {
  const { errors } = useAuctionFilterErrorStore();

  const triggerApi = useCallback(() => {
    const combinedError = Object.values(errors)
      .filter((e) => e !== "")
      .join(", ");

    if (combinedError) {
      console.error(combinedError);
      return;
    }

    onFilterChange();
  }, [errors, onFilterChange]);

  return (
    <div
      className={cn(
        "flex h-fit w-[278px] flex-col gap-5 rounded-sm border border-white bg-gray-900 px-5 py-6",
        className,
      )}
    >
      <PriceSection triggerApi={triggerApi} />
      <Separator />
      <IRSection triggerApi={triggerApi} />
      <Separator />

      <REPAY_TYPESection triggerApi={triggerApi} />
      <Separator />

      <MAT_DTSection triggerApi={triggerApi} />
    </div>
  );
};

export default AuctionFilter;
