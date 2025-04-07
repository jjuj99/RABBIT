import { useEffect, useState } from "react";
import { Label } from "@/shared/ui/label";
import UnitInput from "@/entities/common/ui/UnitInput";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { useAuctionFilterErrorStore } from "@/shared/lib/store/auctionFilterErrorStore";

interface IRSectionProps {
  triggerApi: () => void;
}

const IRSection = ({ triggerApi }: IRSectionProps) => {
  const minIR = useAuctionFilterStore((state) => state.minIr);
  const maxIR = useAuctionFilterStore((state) => state.maxIr);
  const setMinIR = useAuctionFilterStore((state) => state.setMinIr);
  const setMaxIR = useAuctionFilterStore((state) => state.setMaxIr);

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

  const handleMinIRChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMinIR(e.target.value);
  };

  const handleMaxIRChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMaxIR(e.target.value);
  };

  return (
    <div className="flex h-fit w-full flex-col gap-3">
      <div>
        <h2 className="text-base font-bold">이자율</h2>
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
            onChange={handleMinIRChange}
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
            onChange={handleMaxIRChange}
            onBlur={triggerApi}
          />
        </div>
      </div>
    </div>
  );
};

export default IRSection;
