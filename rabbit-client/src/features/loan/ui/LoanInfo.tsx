import { Separator } from "@/shared/ui/Separator";
import { Circle } from "lucide-react";
import { InfoRow } from "@/entities/common/ui/InfoRow";

const LoanInfo = () => {
  return (
    <div className="h-fit w-full rounded-sm bg-gray-800 p-6">
      <div className="flex w-full flex-col gap-2">
        <div className="flex flex-row items-center justify-between">
          <span className="font-bit text-xs font-medium text-white sm:text-base">
            RABBIT#1213
          </span>
          <div className="flex items-center gap-2">
            <Circle
              className="text-brand-primary fill-brand-primary"
              width={8}
              height={8}
            />
            <span className="text-xs font-medium sm:text-base">정상</span>
          </div>
        </div>
        <Separator className="w-full" />
        <div className="flex flex-col gap-2">
          <InfoRow label="계약일" value="2024-04-30" />
          <InfoRow label="채권자" value="홍길동" />
          <InfoRow
            label="지갑 주소"
            value="0xB2e1aBfC4E6dF34eA5C7B8a9E0F1B2C3D4E5F678"
          />
          <InfoRow label="대출 금액" value="1,000,000₩" />
          <InfoRow label="만기시 총 수취액" value="52,000,000₩" />
          <InfoRow label="상환 방식" value="원리금 균등 상관" />
          <InfoRow label="이자율" value="5%" />
          <InfoRow label="연체 이자율" value="5%" />
          <InfoRow label="연체" value="0회" />
        </div>
      </div>
    </div>
  );
};

export default LoanInfo;
