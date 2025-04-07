import { useEffect, useState } from "react";
import { Label } from "@/shared/ui/label";
import { BarRadio, BarRadioItem } from "@/shared/ui/BarRadio";
import { Input } from "@/shared/ui/input";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { useAuctionFilterErrorStore } from "../../../shared/lib/store/auctionFilterErrorStore";

interface MAT_DTSectionProps {
  triggerApi: () => void;
}

const MAT_DTSection: React.FC<MAT_DTSectionProps> = ({ triggerApi }) => {
  const maturity = useAuctionFilterStore((state) => state.matTerm);
  const setMaturity = useAuctionFilterStore((state) => state.setMatTerm);
  const startDate = useAuctionFilterStore((state) => state.matStart);
  const endDate = useAuctionFilterStore((state) => state.matEnd);
  const setStartDate = useAuctionFilterStore((state) => state.setMatStart);
  const setEndDate = useAuctionFilterStore((state) => state.setMatEnd);

  const { setError } = useAuctionFilterErrorStore();
  const [dateError, setDateError] = useState<string>("");

  useEffect(() => {
    let errorMessage = "";
    if (maturity === "directSelect") {
      if (startDate && endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        if (start > end) {
          errorMessage = "종료일이 시작일보다 빠를 수 없습니다.";
        }
      }
    }
    setDateError(errorMessage);
    setError("matDt", errorMessage);
  }, [maturity, startDate, endDate, setError]);

  const handleMaturityChange = (value: string) => {
    setMaturity(value);
  };

  const handleStartDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(e.target.value);
  };

  const handleEndDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEndDate(e.target.value);
  };

  return (
    <div className="flex h-fit w-full flex-col gap-3">
      <h2 className="text-base font-bold">만기일</h2>
      <div>
        <BarRadio
          value={maturity}
          onValueChange={(value) => {
            handleMaturityChange(value);
            triggerApi();
          }}
        >
          <BarRadioItem id="oneMonth" value="1">
            1개월이내
          </BarRadioItem>
          <BarRadioItem id="threeMonths" value="3">
            3개월이내
          </BarRadioItem>
          <BarRadioItem id="sixMonths" value="6">
            6개월이내
          </BarRadioItem>
          <BarRadioItem id="oneYear" value="12">
            1년이내
          </BarRadioItem>
          <BarRadioItem id="directSelect" value="directSelect">
            직접선택
          </BarRadioItem>
        </BarRadio>
        {maturity === "directSelect" && (
          <div className="mt-4 flex flex-col justify-between gap-4">
            <div className="flex flex-col gap-1">
              {dateError && (
                <div className="text-fail text-xs">{dateError}</div>
              )}
              <div>
                <Label htmlFor="startDate">시작일</Label>
                <Input
                  id="startDate"
                  type="date"
                  borderType="white"
                  value={startDate}
                  onChange={handleStartDateChange}
                  onBlur={triggerApi}
                />
              </div>
            </div>
            <div>
              <Label htmlFor="endDate">종료일</Label>
              <Input
                id="endDate"
                type="date"
                borderType="white"
                value={endDate}
                onChange={handleEndDateChange}
                onBlur={triggerApi}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MAT_DTSection;
