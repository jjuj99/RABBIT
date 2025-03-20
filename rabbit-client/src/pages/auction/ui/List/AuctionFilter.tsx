import { useState } from "react";
import UnitInput from "@/entities/common/ui/UnitInput";
import { Checkbox } from "@/shared/ui/checkbox";
import { MenubarSeparator } from "@/shared/ui/menubar";
import { Label } from "@/shared/ui/label";

interface PaymentType {
  id: string;
  label: string;
  checked: boolean;
}

const AuctionFilter = () => {
  const initialPaymentTypes: PaymentType[] = [
    { id: "equal-pi", label: "원리금 균등 상환", checked: false },
    { id: "equal-principal", label: "원금 균등 상환", checked: false },
    { id: "bullet", label: "만기 일시 상환", checked: false },
  ];

  const [paymentTypes, setPaymentTypes] =
    useState<PaymentType[]>(initialPaymentTypes);

  const handleCheckboxChange = (id: string, newChecked: boolean) => {
    const updatedTypes = paymentTypes.map((item) =>
      item.id === id ? { ...item, checked: newChecked } : item,
    );
    setPaymentTypes(updatedTypes);
  };

  return (
    <div className="flex h-[800px] w-[278px] flex-col gap-6 rounded-sm border border-white bg-gray-900 px-5 py-6">
      {/* 가격 섹션 */}
      <div className="flex h-fit w-full flex-col gap-3">
        <label className="text-base font-bold">가격</label>
        <div className="flex flex-col gap-4 px-2">
          <div>
            <Label id="minPrice">최소 가격</Label>
            <UnitInput
              id="minPrice"
              type="number"
              unit="원"
              borderType="white"
            />
          </div>
          <div>
            <Label id="maxPrice">최대 가격</Label>
            <UnitInput
              id="maxPrice"
              type="number"
              unit="원"
              borderType="white"
              className="w-full"
            />
          </div>
        </div>
      </div>
      <MenubarSeparator className="h-[0.2px] bg-white" />
      {/* 수익률 섹션 */}
      <div className="flex h-fit w-full flex-col gap-3">
        <label className="text-base font-bold">수익률</label>
        <div className="flex flex-row gap-4 px-2">
          <div>
            <Label id="minyield">최소 수익률</Label>
            <UnitInput
              id="minyield"
              type="number"
              unit="%"
              borderType="white"
              className="w-full"
            />
          </div>
          <div>
            <Label id="maxyield">최대 수익률</Label>
            <UnitInput
              id="maxyield"
              type="number"
              unit="%"
              borderType="white"
              className="w-full"
            />
          </div>
        </div>
      </div>
      <MenubarSeparator className="h-[0.2px] bg-white" />
      <div className="flex h-fit w-full flex-col gap-3">
        <label className="text-base font-bold">종류</label>
        <div className="flex flex-col gap-1">
          {paymentTypes.map((item) => (
            <div
              key={item.id}
              className="flex w-full flex-row items-center justify-between px-2 py-1"
            >
              <span>{item.label}</span>
              <Checkbox
                checkboxType="default"
                checked={item.checked}
                onCheckedChange={(newChecked: boolean) =>
                  handleCheckboxChange(item.id, newChecked)
                }
              />
            </div>
          ))}
        </div>
      </div>
      <MenubarSeparator className="h-[0.2px] bg-white" />
    </div>
  );
};

export default AuctionFilter;
