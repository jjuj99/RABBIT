import { useEffect, useState } from "react";
import { Label } from "@/shared/ui/label";
import UnitInput from "@/entities/common/ui/UnitInput";
import {
  useAuctionFilterErrorStore,
  useAuctionFilterStore,
} from "../../../shared/lib/store/auctionFilterStore";

interface IRSectionProps {
  triggerApi: () => void;
}

const IRSection = ({ triggerApi }: IRSectionProps) => {
  const minIR = useAuctionFilterStore((state) => state.minIR);
  const maxIR = useAuctionFilterStore((state) => state.maxIR);
  const setMinIR = useAuctionFilterStore((state) => state.setMinIR);
  const setMaxIR = useAuctionFilterStore((state) => state.setMaxIR);

  const [errors, setErrors] = useState<string>("");
  const { setError } = useAuctionFilterErrorStore();

  useEffect(() => {
    let errorMessage = "";
    if (minIR !== "" && Number(minIR) < 0) {
      errorMessage = "최소 이자율은 0% 이상이어야 합니다.";
    } else if (maxIR !== "" && Number(maxIR) > 20) {
      errorMessage = "법정 최고 이자율은 20%입니다.";
    } else if (minIR !== "" && Number(minIR) > 20) {
      errorMessage = "법정 최고 이자율은 20%입니다.";
    } else if (minIR !== "" && maxIR !== "" && Number(minIR) > Number(maxIR)) {
      errorMessage = "최소 이자율은 최대 이자율보다 높을 수 없습니다.";
    }
    setError("ir", errorMessage);
    setErrors(errorMessage);
  }, [minIR, maxIR, setError]);

  return (
    <div className="flex h-fit w-full flex-col gap-3">
      <div>
        <h1 className="text-base font-bold">이자율</h1>
        {errors && <div className="text-fail text-xs">{errors}</div>}
      </div>
      <div className="flex flex-row gap-4 px-2">
        <div>
          <Label htmlFor="minIR">최소 이자율</Label>
          <UnitInput
            id="minIR"
            type="number"
            unit="%"
            borderType="white"
            value={minIR}
            onChange={(e) => setMinIR(e.target.value)}
            onBlur={triggerApi}
          />
        </div>
        <div>
          <Label htmlFor="maxIR">최대 이자율</Label>
          <UnitInput
            id="maxIR"
            type="number"
            unit="%"
            borderType="white"
            value={maxIR}
            onChange={(e) => setMaxIR(e.target.value)}
            onBlur={triggerApi}
          />
        </div>
      </div>
    </div>
  );
};

export default IRSection;
