import { useEffect, useState } from "react";
import { Label } from "@/shared/ui/label";
import UnitInput from "@/entities/common/ui/UnitInput";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { useAuctionFilterErrorStore } from "@/shared/lib/store/auctionFilterErrorStore";

interface PriceSectionProps {
  triggerApi: () => void;
}

const PriceSection = ({ triggerApi }: PriceSectionProps) => {
  const minPrice = useAuctionFilterStore((state) => state.minPrice);
  const maxPrice = useAuctionFilterStore((state) => state.maxPrice);
  const setMinPrice = useAuctionFilterStore((state) => state.setMinPrice);
  const setMaxPrice = useAuctionFilterStore((state) => state.setMaxPrice);

  const [errors, setErrors] = useState<string>("");
  const { setError } = useAuctionFilterErrorStore();

  useEffect(() => {
    let errorMessage = "";
    if (minPrice !== "" && Number(minPrice) < 0) {
      errorMessage = "최소 가격은 0원 이상이어야 합니다.";
    } else if (maxPrice !== "" && Number(maxPrice) < 0) {
      errorMessage = "최대 가격은 0원 이상이어야 합니다.";
    } else if (
      minPrice !== "" &&
      maxPrice !== "" &&
      Number(minPrice) > Number(maxPrice)
    ) {
      errorMessage = "최소 가격은 최대 가격보다 작아야 합니다.";
    }
    setError("price", errorMessage);
    setErrors(errorMessage);
  }, [minPrice, maxPrice, setError]);

  return (
    <div className="flex h-fit w-full flex-col gap-3">
      <div>
        <h2 className="text-base font-bold">가격</h2>
        {errors && <div className="text-fail text-xs">{errors}</div>}
      </div>
      <div className="flex flex-col gap-4 px-2">
        <div>
          <Label htmlFor="minPrice">최소 가격</Label>
          <UnitInput
            id="minPrice"
            type="number"
            unit="원"
            borderType="white"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            onBlur={triggerApi}
          />
        </div>
        <div>
          <Label htmlFor="maxPrice">최대 가격</Label>
          <UnitInput
            id="maxPrice"
            type="number"
            unit="원"
            borderType="white"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            onBlur={triggerApi}
          />
        </div>
      </div>
    </div>
  );
};

export default PriceSection;
