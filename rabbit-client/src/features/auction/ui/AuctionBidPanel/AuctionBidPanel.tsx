import UnitInput from "@/entities/common/ui/UnitInput";
import { Button } from "@/shared/ui/button";
import { useState } from "react";

interface AuctionbidpanelProps {
  CBP: number;
  amount: number;
}

const Auctionbidpanel = ({ CBP, amount }: AuctionbidpanelProps) => {
  const [bidPrice, setBidPrice] = useState(CBP);

  // 금액 단위 (예: 1만 = 10,000)
  const increments = {
    one: 10000,
    ten: 100000,
    hundred: 1000000,
    thousand: 10000000,
  };

  // bidPrice를 API에 전달하는 함수
  const handleBidSubmit = async () => {
    // API 호출
    alert(`입찰가: ${bidPrice}`);
  };

  return (
    <div className="bg-radial-lg flex h-fit w-full flex-col gap-3 rounded-sm border border-white px-8 py-6">
      <div className="flex flex-col gap-2">
        <h1 className="text-xl font-medium">현재 입찰가</h1>
        <div>
          <span className="font-partial text-brand-gradient text-4xl">
            {CBP.toLocaleString()}
          </span>
          <span className="font-pixel text-brand-gradient px-1 text-2xl">
            RAB
          </span>
        </div>
      </div>

      <div className="flex flex-col gap-2">
        <h3 className="flex gap-1 text-white">
          <span className="font-normal">보유 금액</span>
          <span className="font-semibold">{amount.toLocaleString()}</span>
          <span className="font-pixel flex items-end text-sm">RAB</span>
        </h3>
        <div className="flex gap-2">
          <Button
            variant="default"
            className="h-[28px] w-[102px] bg-black"
            onClick={() => setBidPrice((prev) => prev + increments.one)}
          >
            <span className="text-sm font-semibold">+1만</span>
          </Button>
          <Button
            variant="default"
            className="h-[28px] w-[102px] bg-black"
            onClick={() => setBidPrice((prev) => prev + increments.ten)}
          >
            <span className="text-sm font-semibold">+10만</span>
          </Button>
          <Button
            variant="default"
            className="h-[28px] w-[102px] bg-black"
            onClick={() => setBidPrice((prev) => prev + increments.hundred)}
          >
            <span className="text-sm font-semibold">+100만</span>
          </Button>
          <Button
            variant="default"
            className="h-[28px] w-[102px] bg-black"
            onClick={() => setBidPrice((prev) => prev + increments.thousand)}
          >
            <span className="text-sm font-semibold">+1000만</span>
          </Button>
          <Button
            variant="default"
            className="h-[28px] w-[102px] bg-black"
            onClick={() => setBidPrice(CBP)}
          >
            <span className="text-sm font-semibold">초기화</span>
          </Button>
        </div>
        <div className="flex w-full gap-2">
          <UnitInput
            value={bidPrice}
            unit="원"
            type="number"
            warraperclassName="w-full"
            className="w-full bg-white"
            onChange={(e) => setBidPrice(Number(e.target.value))}
          />
          <Button variant="positive" onClick={handleBidSubmit}>
            입찰하기
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Auctionbidpanel;
