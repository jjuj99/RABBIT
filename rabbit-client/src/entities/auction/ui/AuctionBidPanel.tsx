import UnitInput from "@/entities/common/ui/UnitInput";
import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import { SubmitAuctionBidAPI } from "@/features/auction/api/auctionApi";
import { Button } from "@/shared/ui/button";
import { useState } from "react";
import { useParams } from "react-router";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/shared/ui/dialog";
import LoadingOverlay from "@/widget/common/ui/LoadingOverray";

interface AuctionBidPanelProps {
  CBP?: number;
}

const AuctionBidPanel = ({ CBP = 0 }: AuctionBidPanelProps) => {
  const [bidPrice, setBidPrice] = useState(CBP);
  const { auctionId } = useParams<{ auctionId: string }>();
  const { balance } = useGetBalance();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [dialogMessage, setDialogMessage] = useState("");
  const [dialogType, setDialogType] = useState<"success" | "error">("success");
  const [isBidding, setIsBidding] = useState(false);

  // 금액 단위 (예: 1만 = 10,000)
  const increments = {
    one: 10000,
    ten: 100000,
    hundred: 1000000,
    thousand: 10000000,
  };

  // bidPrice를 API에 전달하는 함수
  const handleBidSubmit = async () => {
    if (bidPrice > balance) {
      setDialogMessage("보유한 RAB이 부족합니다.");
      setDialogType("error");
      setIsDialogOpen(true);
      return;
    }

    try {
      setIsBidding(true);
      const response = await SubmitAuctionBidAPI(Number(auctionId), bidPrice);
      if (response.status === "ERROR") {
        setDialogMessage(response.error?.message || "입찰에 실패했습니다.");
        setDialogType("error");
        setIsDialogOpen(true);
        return;
      }
      setDialogMessage(response.data?.message || "입찰이 완료되었습니다.");
      setDialogType("success");
      setIsDialogOpen(true);
    } catch {
      setDialogMessage("입찰 중 오류가 발생했습니다.");
      setDialogType("error");
      setIsDialogOpen(true);
    } finally {
      setIsBidding(false);
    }
  };

  return (
    <>
      <LoadingOverlay isLoading={isBidding} content="입찰 처리 중..." />
      <div className="bg-radial-lg flex h-fit w-full flex-col gap-1 rounded-sm border border-white px-4 py-4 sm:gap-3 sm:px-8 sm:py-6">
        <div className="flex flex-col gap-0 sm:gap-2">
          <h2 className="text-sm font-medium sm:text-xl">현재 입찰가</h2>
          <div>
            <span className="font-partial text-brand-gradient text-lg sm:text-4xl">
              {CBP.toLocaleString()}
            </span>
            <span className="font-pixel text-brand-gradient text px-1 sm:text-2xl">
              RAB
            </span>
          </div>
        </div>
        <div className="item flex flex-col gap-2 sm:gap-4">
          <h3 className="flex items-center gap-1 text-white">
            <span className="text-sm font-normal sm:text-lg">보유 금액</span>
            <div className="flex gap-0.5">
              <span className="text-sm font-semibold sm:text-xl">
                {balance.toLocaleString()}
              </span>
              <span className="font-pixel mb-[+1px] flex items-end text-xs sm:text-lg">
                RAB
              </span>
            </div>
          </h3>
          <div className="hidden flex-wrap gap-2 sm:flex">
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
              unit="RAB"
              type="number"
              wrapperClassName="w-full"
              className="w-full bg-white"
              onChange={(e) => setBidPrice(Number(e.target.value))}
            />
            <Button variant="positive" onClick={handleBidSubmit}>
              입찰하기
            </Button>
          </div>
        </div>
      </div>
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {dialogType === "success" ? "성공" : "오류"}
            </DialogTitle>
            <DialogDescription>{dialogMessage}</DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="default" onClick={() => setIsDialogOpen(false)}>
              확인
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default AuctionBidPanel;
