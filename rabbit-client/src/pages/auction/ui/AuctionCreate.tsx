import { UnitInput } from "@/entities/common";
import { MyNFTcard } from "@/entities/NFT/ui/MyNFTcard";
import { createAuctionAPI } from "@/features/auction/api/auctionApi";
import { PNInfoListResponse } from "@/features/auction/types/response";
import { Button } from "@/shared/ui/button";
import { Sheet, SheetContent, SheetOverlay } from "@/shared/ui/CustomSheet";
import { Separator } from "@/shared/ui/Separator";

import { useState } from "react";

const AuctionCreate = () => {
  const [selectedItem, setSelectedItem] = useState<PNInfoListResponse | null>(
    null,
  );
  const [startPrice, setStartPrice] = useState<number>(0);
  const [isOpen, setIsOpen] = useState(false);
  const [step, setStep] = useState(1);
  const [endDateday, setendDateday] = useState(0);
  const [endDatehour, setendDatehour] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const handleDayChange = (value: number) => {
    if (value > 7) {
      setError("7일을 초과할 수 없습니다.");
      return;
    }
    const totalDays = value + Math.floor(endDatehour / 24);
    if (totalDays > 7) {
      setError("7일을 초과할 수 없습니다.");
      return;
    }
    setError(null);
    setendDateday(value);
  };

  const handleHourChange = (value: number) => {
    if (value >= 24) {
      const newDays = endDateday + Math.floor(value / 24);
      if (newDays > 7) {
        setError("7일을 초과할 수 없습니다.");
        return;
      }
      setendDateday(newDays);
      setendDatehour(value % 24);
    } else {
      const totalDays = endDateday + Math.floor(value / 24);
      if (totalDays >= 7) {
        setError("7일을 초과할 수 없습니다.");
        return;
      }
      setendDatehour(value);
    }
    setError(null);
  };

  const calculateEndDate = () => {
    const now = new Date();
    const endDate = new Date(now);
    endDate.setDate(endDate.getDate() + endDateday);
    endDate.setHours(endDate.getHours() + endDatehour);
    return endDate;
  };

  const formatDisplayDate = (date: Date) => {
    return date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  // 임시 데이터
  const mockItem: PNInfoListResponse = {
    auction_id: 1,
    price: 1000000,
    end_date: "2025-12-31T23:59:59",
    ir: 5.5,
    created_at: "2024-01-01T00:00:00",
    repay_type: "BULLET",
    total_amount: 10000000,
    mat_dt: "2025-01-01",
    dir: 100,
    la: 1000000,
    earlypay_flag: false,
    earlypay_fee: 0,
    credit_score: 800,
    def_cnt: 0,
  };

  const handleCardClick = (item: PNInfoListResponse) => {
    setSelectedItem(item);
    setStartPrice(item.total_amount);
    setIsOpen(true);
  };

  const handleNext = () => {
    setStep((prev) => prev + 1);
  };

  const handlePrev = () => {
    setStep((prev) => prev - 1);
  };

  const handleClose = () => {
    setIsOpen(false);
    setStep(1);
    setendDateday(0);
    setendDatehour(0);
    setError(null);
  };

  const handleSubmit = async () => {
    if (!selectedItem) return;
    try {
      await createAuctionAPI({
        minimum_bid: startPrice,
        end_date: calculateEndDate().toISOString(),
        token_id: "selectedItem.token_id",
        seller_sign: "selectedItem.seller_sign",
      });
      handleClose();
    } catch (error) {
      console.error("경매 생성 실패:", error);
    }
  };

  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div className="flex flex-row items-center">
              <span className="text-brand-primary text-xl font-bold">
                경매 시작가
              </span>
              <span className="text-xl">를 입력하세요</span>
            </div>
            {selectedItem && <MyNFTcard item={selectedItem} />}
            <UnitInput
              type="number"
              value={startPrice}
              unit="RAB"
              onChange={(e) => setStartPrice(Number(e.target.value))}
              wrapperClassName="w-[300px]"
              borderType="white"
            />
            <div className="flex gap-5">
              <Button
                onClick={handleClose}
                variant="secondary"
                className="w-[140px]"
              >
                취소
              </Button>
              <Button
                onClick={handleNext}
                variant="primary"
                className="w-[140px]"
              >
                다음
              </Button>
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div className="flex flex-col items-center">
              <div className="flex flex-row items-center">
                <span className="text-brand-primary text-xl font-bold">
                  경매 기간
                </span>
                <span className="text-xl">을 입력하세요</span>{" "}
              </div>
              {error && <div className="text-fail">{error}</div>}
            </div>

            <div className="flex w-[300px] gap-5">
              <UnitInput
                type="number"
                value={endDateday}
                unit="일"
                onChange={(e) => handleDayChange(Number(e.target.value))}
                borderType="white"
              />
              <UnitInput
                type="number"
                value={endDatehour}
                unit="시간"
                onChange={(e) => handleHourChange(Number(e.target.value))}
                borderType="white"
              />
            </div>
            <div className="text-lg">
              {formatDisplayDate(calculateEndDate())}까지
            </div>
            <div className="flex gap-5">
              <Button
                onClick={handlePrev}
                variant="secondary"
                className="w-[140px]"
              >
                이전
              </Button>
              <Button
                onClick={handleNext}
                variant="primary"
                className="w-[140px]"
                disabled={!!error || (endDateday === 0 && endDatehour === 0)}
              >
                다음
              </Button>
            </div>
          </>
        );
      case 3:
        return (
          <>
            <div className="flex flex-col items-center gap-4">
              <div className="mb-4 flex flex-col items-center">
                <div className="flex items-center gap-1">
                  <span className="text-brand-primary text-lg font-bold">
                    등록 시
                  </span>
                  <span className="text-whites-primary text-lg font-medium">
                    경매가 즉시 시작되며
                  </span>
                </div>
                <div className="flex items-center gap-1">
                  <span className="text-lg font-medium text-white">
                    입찰 발생시
                  </span>
                  <span className="text-fail text-xl font-bold">
                    경매를 취소하실 수 없습니다.
                  </span>
                </div>
              </div>

              {selectedItem && <MyNFTcard item={selectedItem} />}
              <div className="flex w-full flex-col items-start gap-3 rounded-sm bg-gray-900 py-4">
                <div className="flex w-full flex-col gap-0">
                  <div className="flex flex-row items-start justify-between gap-2">
                    <span className="text-lg font-light text-gray-50">
                      경매 마감일
                    </span>
                    <span className="text-xl font-medium">
                      {formatDisplayDate(calculateEndDate())}
                    </span>
                  </div>
                  <Separator className="w-full" />
                </div>
                <div className="flex w-full flex-col gap-0">
                  <div className="flex w-full flex-row justify-between gap-2">
                    <span className="text-lg font-light text-gray-50">
                      경매 시작가
                    </span>
                    <span className="text-xl font-medium">
                      {startPrice.toLocaleString()} RAB
                    </span>
                  </div>
                  <Separator className="w-full" />
                </div>
              </div>
              <div className="flex gap-5">
                <Button
                  onClick={handleSubmit}
                  variant="primary"
                  className="w-[140px]"
                >
                  확인
                </Button>
                <Button
                  onClick={handlePrev}
                  variant="secondary"
                  className="w-[140px]"
                >
                  이전
                </Button>
              </div>
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <section className="flex flex-col items-center justify-center gap-9 px-6 pt-9">
      <div className="flex flex-col items-center gap-4">
        <h2 className="text-3xl font-semibold whitespace-nowrap">경매 생성</h2>
        <h3 className="text-text-secondary text-lg">
          등록시 경매가 시작되며, 구매자의 입찰 발생시 경매를 취소할 수
          없습니다.
        </h3>
      </div>
      <div className="flex w-full flex-col items-start gap-4 px-7">
        <h3 className="text-lg">보유중인 차용증</h3>
        <ul className="flex flex-wrap gap-6">
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>{" "}
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>{" "}
          <div
            onClick={() => handleCardClick(mockItem)}
            className="cursor-pointer"
          >
            <MyNFTcard item={mockItem} />
          </div>
        </ul>
      </div>
      <Sheet
        open={isOpen}
        onOpenChange={(open) => {
          setIsOpen(open);
          if (!open) {
            setStep(1);
          }
        }}
      >
        <SheetOverlay className="bg-black/40" />
        <SheetContent side="right" className="w-[30vw] bg-transparent">
          <div className="flex h-full flex-col items-center justify-center gap-8">
            {renderStepContent()}
          </div>
        </SheetContent>
      </Sheet>
    </section>
  );
};

export default AuctionCreate;
