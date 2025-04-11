import { useState } from "react";
import { ethers } from "ethers";
import { Button } from "@/shared/ui/button";
import { Sheet, SheetContent, SheetOverlay } from "@/shared/ui/CustomSheet";
import { Separator } from "@/shared/ui/Separator";
import { getMetaMaskProvider } from "@/entities/wallet/utils/getMetaMaskProvider";
import getWalletAddress from "@/entities/wallet/utils/getWalletAddress";
import {
  createAuctionAPI,
  getAvailableAuctionsAPI,
} from "@/features/auction/api/auctionApi";
import { AvailableAuctionsResponse } from "@/features/auction/types/response";
import { MyNFTcard } from "@/entities/NFT/ui/MyNFTcard";
import { NFTSkeleton } from "@/entities/NFT/ui/NFTSkeleton";
import { UnitInput } from "@/entities/common";
import promissoryNoteAbi from "@/shared/lib/web3/ABI/PromissoryNoteABI.json";
import { deleteAuctionAPI } from "@/features/auction/api/auctionApi";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import LoadingOverlay from "@/widget/common/ui/LoadingOverray";

// NFT 및 경매 컨트랙트 주소는 환경변수나 상수로 설정
const PROMISSORYNOTE_AUCTION_ADDRESS = import.meta.env
  .VITE_RABBIT_PROMISSORYNOTE_AUCTION_ADDRESS;

const PROMISSORY_NOTE_ADDRESS = import.meta.env
  .VITE_RABBIT_PROMISSORYNOTE_ADDRESS;
// ABI는 별도 모듈에서 import하거나, 아래처럼 직접 포함할 수 있습니다.

const AuctionCreate = () => {
  const [selectedItem, setSelectedItem] =
    useState<AvailableAuctionsResponse | null>(null);
  const [startPrice, setStartPrice] = useState<number>(0);
  const [isOpen, setIsOpen] = useState(false);
  const [step, setStep] = useState(1);
  const [endDateday, setEndDateday] = useState(0);
  const [endDatehour, setEndDatehour] = useState(0);
  const [endDateminute, setEndDateminute] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // 모의 데이터 사용 (실제 서비스에서는 API 호출로 대체)
  const { data: availableAuctions, isLoading: isLoadingAuctions } = useQuery({
    queryKey: ["availableAuctions"],
    queryFn: () => getAvailableAuctionsAPI(),
  });

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
    setEndDateday(value);
  };

  const navigate = useNavigate();

  const handleHourChange = (value: number) => {
    if (value >= 24) {
      const newDays = endDateday + Math.floor(value / 24);
      if (newDays > 7) {
        setError("7일을 초과할 수 없습니다.");
        return;
      }
      setEndDateday(newDays);
      setEndDatehour(value % 24);
    } else {
      const totalDays = endDateday + Math.floor(value / 24);
      if (totalDays >= 7) {
        setError("7일을 초과할 수 없습니다.");
        return;
      }
      setEndDatehour(value);
    }
    setError(null);
  };

  const handleMinuteChange = (value: number) => {
    if (value >= 60) {
      const newHours = endDatehour + Math.floor(value / 60);
      if (newHours >= 24) {
        const newDays = endDateday + Math.floor(newHours / 24);
        if (newDays > 7) {
          setError("7일을 초과할 수 없습니다.");
          return;
        }
        setEndDateday(newDays);
        setEndDatehour(newHours % 24);
      } else {
        setEndDatehour(newHours);
      }
      setEndDateminute(value % 60);
    } else {
      const totalDays =
        endDateday + Math.floor((endDatehour + Math.floor(value / 60)) / 24);
      if (totalDays >= 7) {
        setError("7일을 초과할 수 없습니다.");
        return;
      }
      setEndDateminute(value);
    }
    setError(null);
  };

  const calculateEndDate = () => {
    const now = new Date();
    const endDate = new Date(now);
    endDate.setDate(endDate.getDate() + endDateday);
    endDate.setHours(endDate.getHours() + endDatehour);
    endDate.setMinutes(endDate.getMinutes() + endDateminute);
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

  const handleCardClick = (item: AvailableAuctionsResponse) => {
    setSelectedItem(item);
    setStartPrice(item.totalAmount);
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
    setEndDateday(0);
    setEndDatehour(0);
    setEndDateminute(0);
    setError(null);
  };

  const handleSubmit = async () => {
    if (!selectedItem) {
      setError("경매할 차용증을 선택해주세요.");
      return;
    }
    setIsLoading(true);
    try {
      if (!window.ethereum) {
        throw new Error("메타마스크가 필요합니다");
      }
      const signerprovider = new ethers.BrowserProvider(window.ethereum);
      const signer = await signerprovider.getSigner();

      const provider = await getMetaMaskProvider();

      if (!provider) {
        setError("메타마스크를 찾을 수 없습니다.");
        return;
      }

      // 2. 현재 사용자의 지갑 주소 확인
      const walletInfo = await getWalletAddress({ provider });
      if (!walletInfo || !walletInfo.address) {
        setError("지갑 주소를 가져올 수 없습니다.");
        return;
      }

      console.log(PROMISSORYNOTE_AUCTION_ADDRESS, signer);

      // 3. PromissoryNote 컨트랙트 인스턴스 생성
      const promissoryNoteContract = new ethers.Contract(
        PROMISSORY_NOTE_ADDRESS,
        promissoryNoteAbi,
        signer,
      );

      const response = await createAuctionAPI({
        minimumBid: startPrice,
        endDate: calculateEndDate().toISOString(),
        tokenId: selectedItem?.tokenId.toString() ?? "",
      });

      if (!response.data) {
        console.log("경매 생성에 실패했습니다. api단계에서.");
        throw new Error("경매 생성에 실패했습니다.");
      }

      console.log("db에 저장 성공 경매 ID:", response.data.auctionId);

      try {
        // 4. transferFrom 호출: 현재 소유자(walletInfo.address) → 경매 컨트랙트 주소
        const depositToAuction = await promissoryNoteContract.depositToAuction(
          selectedItem?.tokenId,
        );

        console.log("DepositToAuction 트랜잭션 전송됨:", depositToAuction.hash);
        await depositToAuction.wait();
        console.log("DepositToAuction 트랜잭션 확정됨.");

        handleClose();
        navigate(`/auction/${response.data.auctionId}`);
      } catch (error) {
        console.error("Transfer 실패:", error);
        // transfer 실패 시 생성된 경매 삭제
        if (response.data?.auctionId) {
          await deleteAuctionAPI(response.data.auctionId);
        }
        setError("NFT 전송에 실패했습니다. 경매가 취소되었습니다.");
      }
    } catch (error) {
      console.error("경매 생성 실패:", error);
      setError("경매 생성에 실패했습니다.");
    } finally {
      setIsLoading(false);
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
                <span className="text-xl">을 입력하세요</span>
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
              <UnitInput
                type="number"
                value={endDateminute}
                unit="분"
                onChange={(e) => handleMinuteChange(Number(e.target.value))}
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
                disabled={
                  !!error ||
                  (endDateday === 0 && endDatehour === 0 && endDateminute === 0)
                }
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
                  <span className="text-brand-primary font-bold sm:text-lg">
                    등록 시
                  </span>
                  <span className="text-whites-primary font-medium sm:text-lg">
                    경매가 즉시 시작되며
                  </span>
                </div>
                <div className="flex items-center gap-1">
                  <span className="font-medium text-white sm:text-lg">
                    입찰 발생시
                  </span>
                  <span className="text-fail font-bold sm:text-xl">
                    경매를 취소하실 수 없습니다.
                  </span>
                </div>
              </div>
              {selectedItem && <MyNFTcard item={selectedItem} />}
              <div className="flex w-full flex-col items-start gap-3 rounded-sm bg-gray-900 px-5 py-3 sm:px-0 sm:py-4">
                <div className="flex w-full flex-col gap-0">
                  <div className="flex flex-row items-start justify-between gap-2">
                    <span className="text-sm font-light text-gray-50 sm:text-lg">
                      경매 마감일
                    </span>
                    <span className="text-smfont-medium sm:text-xl">
                      {formatDisplayDate(calculateEndDate())}
                    </span>
                  </div>
                  <Separator className="w-full" />
                </div>
                <div className="flex w-full flex-col gap-0">
                  <div className="flex w-full flex-row justify-between gap-2">
                    <span className="text-sm font-light text-gray-50 sm:text-lg">
                      경매 시작가
                    </span>
                    <span className="text-sm font-medium sm:text-xl">
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
                  disabled={isLoading}
                >
                  {isLoading ? "진행중..." : "확인"}
                </Button>
                <Button
                  onClick={handlePrev}
                  variant="secondary"
                  className="w-[140px]"
                  disabled={isLoading}
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

  // 스켈레톤 카드 6개 배열 생성
  const skeletonCards = Array(6).fill(0);

  return (
    <>
      {/* 로딩 오버레이 - 경매 생성 중일 때 표시 */}
      <LoadingOverlay
        content={[
          "경매 생성 중...",
          "최대 2분 소요됩니다...",
          "Seporia 네트워크에 연결중...",
          "메타마스크 확인 중...",
          "차용증 예치중...",
          "경매 확인 중..",
          "진짜 생성 중..",
          "거의 생성 중...",
        ]}
        isLoading={isLoading}
      />
      <section className="flex flex-col items-center justify-center gap-9 px-2 pt-9 sm:px-6">
        <div className="flex flex-col items-center gap-4">
          <h2 className="text-xl font-semibold whitespace-nowrap sm:text-3xl">
            경매 생성
          </h2>
          <h3 className="text-text-secondary flex flex-wrap justify-center text-sm sm:text-lg">
            <span>등록시 경매가 시작되며, </span>
            <span>구매자의 입찰 발생시 경매를 취소할 수 없습니다.</span>
          </h3>
        </div>
        <div className="flex w-full flex-col items-start gap-4 sm:px-7">
          <h3 className="mb-8 text-2xl">
            경매에 등록할 차용증을 선택해주세요.
          </h3>

          {/* 데이터 로딩 상태 표시 - 스켈레톤 UI로 대체 */}
          {isLoadingAuctions ? (
            <ul className="flex flex-wrap gap-6">
              {skeletonCards.map((_, index) => (
                <li key={`skeleton-${index}`}>
                  <NFTSkeleton />
                </li>
              ))}
            </ul>
          ) : (
            <ul className="flex min-h-[70vh] flex-wrap justify-center gap-6">
              {availableAuctions?.data?.content.length ? (
                availableAuctions.data.content.map(
                  (item: AvailableAuctionsResponse) => (
                    <div
                      key={item.tokenId}
                      onClick={() => handleCardClick(item)}
                      className="cursor-pointer"
                    >
                      <MyNFTcard item={item} />
                    </div>
                  ),
                )
              ) : (
                <div className="w-full py-10 text-center text-gray-400">
                  경매 가능한 차용증이 없습니다.
                </div>
              )}
            </ul>
          )}
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
          <SheetContent
            side="right"
            className="w-[100vw] bg-transparent lg:w-[40vw]"
          >
            <div className="flex h-full flex-col items-center justify-center gap-8">
              {renderStepContent()}
            </div>
          </SheetContent>
        </Sheet>
      </section>
    </>
  );
};

export default AuctionCreate;
