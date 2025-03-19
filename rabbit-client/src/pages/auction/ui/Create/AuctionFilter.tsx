import UnitInput from "@/entities/common/ui/UnitInput";
import { MenubarSeparator } from "@/shared/ui/menubar";

const AuctionFilter = () => {
  return (
    <div className="flex h-[800px] w-[278px] flex-col gap-6 rounded-sm border border-white bg-gray-900 px-5 py-6">
      <div className="flex h-fit w-full flex-col gap-3">
        <label className="text-base font-bold">가격</label>
        <div className="flex flex-col gap-4 px-2">
          <UnitInput
            type="number"
            unit="원"
            borderType="white"
            className="w-full"
            label="최소 가격"
          />
          <UnitInput
            type="number"
            unit="원"
            borderType="white"
            className="w-full"
            label="최대 가격"
          />
        </div>
      </div>
      <MenubarSeparator className="h-[0.2px] bg-white" />
      <div className="flex h-fit w-full flex-col gap-3">
        <label className="text-base font-bold">수익률</label>
        <div className="flex flex-row gap-4 px-2">
          <UnitInput
            type="number"
            unit="%"
            borderType="white"
            className="w-full"
            label="최소 가격"
          />
          <UnitInput
            type="number"
            unit="%"
            borderType="white"
            className="w-full"
            label="최대 가격"
          />
        </div>
      </div>
    </div>
  );
};

export default AuctionFilter;
