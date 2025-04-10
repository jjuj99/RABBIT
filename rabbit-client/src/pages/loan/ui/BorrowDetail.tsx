import { InfoRow } from "@/entities/common/ui/InfoRow";
import LoanInfo from "@/features/loan/ui/LoanInfo";
import { Separator } from "@/shared/ui/Separator";
import { ContractPeriod } from "@/entities/loan/ui/ContractPeriod";
import { Button } from "@/shared/ui/button";
import NFTEventList from "@/entities/common/ui/NFTEventList";
import NFTEventListMobile from "@/entities/common/ui/NFTEventListMobile";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import {
  useMutation,
  useQueryClient,
  useSuspenseQuery,
} from "@tanstack/react-query";
import {
  getBorrowDetailAPI,
  earlypayAPI,
  getloanEventAPI,
} from "@/entities/loan/api/loanApi";
import { useNavigate, useParams } from "react-router";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/ui/dialog";
import { Input } from "@/shared/ui/input";
import { useState } from "react";
import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import { cn } from "@/shared/lib/utils";
import { Suspense } from "react";

const EventListSection = ({
  isDesktop,
  contractId,
}: {
  isDesktop: boolean;
  contractId: string;
}) => {
  const { data: eventData } = useSuspenseQuery({
    queryKey: ["loanEvent", contractId],
    queryFn: () => getloanEventAPI(contractId),
  });

  return (
    <div className="w-full rounded-sm bg-gray-900">
      {isDesktop ? (
        <NFTEventList data={eventData.data} />
      ) : (
        <NFTEventListMobile data={eventData.data} />
      )}
    </div>
  );
};

const BorrowDetail = () => {
  const isDesktop = useMediaQuery("lg");
  const [isEarlyRepaymentOpen, setIsEarlyRepaymentOpen] = useState(false);
  const [repaymentAmount, setRepaymentAmount] = useState<number>(0);

  const { balance } = useGetBalance();
  const [isImageLoaded, setIsImageLoaded] = useState(false);
  const { contractId } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data } = useSuspenseQuery({
    queryKey: ["borrowDetail", contractId],
    queryFn: () => getBorrowDetailAPI(contractId!),
  });

  const earlyRepaymentMutation = useMutation({
    mutationFn: (amount: number) => earlypayAPI(contractId!, amount),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["borrowDetail", contractId] });
      setIsEarlyRepaymentOpen(false);
      setRepaymentAmount(0);
    },
  });

  const handleAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!data?.data) return;
    const value = e.target.value.replace(/[^0-9]/g, "");
    const numericValue = Number(value);
    const maxAmount = Math.min(data.data.remainingPrincipal, balance);
    if (numericValue <= maxAmount) {
      setRepaymentAmount(numericValue);
    }
  };

  const calculateTotalAmount = () => {
    if (!data?.data) return 0;
    const fee = (repaymentAmount * data.data.earlypayFee) / 100;
    return repaymentAmount + fee;
  };

  const isExceedBalance = () => {
    return calculateTotalAmount() > balance;
  };

  const handleEarlyRepayment = () => {
    if (!data?.data) return;
    if (
      !isNaN(repaymentAmount) &&
      repaymentAmount > 0 &&
      repaymentAmount <= data.data.remainingPrincipal
    ) {
      earlyRepaymentMutation.mutate(repaymentAmount);
    }
  };

  if (!contractId) {
    navigate("/loan");
    return null;
  }

  if (!data?.data) {
    return <div>데이터가 없습니다.</div>;
  }

  return (
    <div className="flex flex-col gap-8">
      <h2 className="text-xl font-bold sm:text-2xl">채무 상세</h2>
      <div className="flex h-fit flex-col items-center gap-4 md:flex-row md:items-start">
        <div className="relative aspect-square w-full max-w-[593px] rounded-sm">
          {!isImageLoaded && (
            <div className="absolute inset-0 animate-pulse rounded-sm bg-gray-800"></div>
          )}
          <img
            onLoad={() => setIsImageLoaded(true)}
            src={data.data.nftImage}
            className={cn(
              "h-full w-full rounded-sm object-cover transition-opacity duration-500",
              isImageLoaded ? "opacity-100" : "opacity-0",
            )}
          />
        </div>

        <div className="flex w-full flex-col gap-2">
          <LoanInfo
            tokenId={data.data.tokenId}
            crName={data.data.crName}
            crWallet={data.data.crWallet}
            la={data.data.la}
            remainingPrincipal={data.data.remainingPrincipal}
            repayType={data.data.repayType}
            ir={data.data.ir}
            dir={data.data.dir}
            defCnt={data.data.defCnt}
            contractDt={data.data.contractDt}
            pnStatus={data.data.pnStatus}
            accel={data.data.accel}
            accelDir={data.data.accelDir}
          />
          <div className="flex h-full w-full flex-col gap-2">
            <div className="flex h-full flex-col gap-2 lg:flex-col">
              <div className="flex h-full w-full flex-col justify-center gap-2 rounded-sm bg-gray-800 px-4 py-3">
                <InfoRow label="다음 상환 일자" value={data.data.nextMpDt} />
                <InfoRow
                  label="상환 금액"
                  value={data.data.nextAmount.toLocaleString()}
                />
              </div>
              <div className="flex h-full w-full flex-row gap-2">
                <div className="flex h-full w-full min-w-[180px] flex-col justify-center gap-2 rounded-sm bg-gray-800 px-4 py-3">
                  <InfoRow
                    label="중도 상환 수수료"
                    value={`${data.data.earlypayFee}%`}
                  />
                  {data.data.earlypayFlag ? (
                    <Button
                      variant="primary"
                      size="sm"
                      className="w-full"
                      onClick={() => setIsEarlyRepaymentOpen(true)}
                    >
                      중도 상환
                    </Button>
                  ) : (
                    <div className="text-center text-sm text-gray-400">
                      중도 상환 불가능
                    </div>
                  )}
                </div>
                <div className="flex h-full w-full flex-col gap-2 rounded-sm bg-gray-800 px-4 py-3">
                  <div className="whitespace-nowrap text-gray-100">
                    계약서 상세보기
                  </div>
                  <Button
                    variant="primary"
                    size="sm"
                    className="w-full"
                    onClick={() => {
                      if (data?.data?.addTermsHash) {
                        window.open(
                          data.data.addTermsHash,
                          "_blank",
                          "noopener,noreferrer",
                        );
                      }
                    }}
                  >
                    확인
                  </Button>
                </div>
              </div>
            </div>
            <Separator />
          </div>
        </div>
      </div>
      {data.data.addTerms && (
        <div className="flex flex-col gap-2">
          <h2 className="text-xl font-bold sm:text-2xl">특약사항</h2>
          <div className="rounded-sm bg-gray-800 p-4">
            <p className="text-white">{data.data.addTerms}</p>
          </div>
        </div>
      )}
      <ContractPeriod
        startDate={data.data.contractDt}
        endDate={data.data.matDt}
      />
      <h2 className="text-xl font-bold sm:text-2xl">이벤트 리스트</h2>
      <Suspense fallback={<div>이벤트 정보를 불러오는 중입니다...</div>}>
        <EventListSection isDesktop={isDesktop} contractId={contractId} />
      </Suspense>
      <Dialog
        open={isEarlyRepaymentOpen}
        onOpenChange={setIsEarlyRepaymentOpen}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>중도상환</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              <label className="text-sm text-gray-100">상환 금액</label>
              <Input
                value={repaymentAmount || ""}
                onChange={handleAmountChange}
                placeholder="상환할 금액을 입력하세요"
                type="number"
                min="0"
              />
              <p className="text-sm text-gray-100">
                남은 원금: {data.data.remainingPrincipal.toLocaleString()} 원
              </p>
              <p className="text-sm text-gray-100">
                보유 금액: {balance.toLocaleString()} 원
              </p>
              {repaymentAmount && (
                <>
                  <p className="text-sm text-gray-100">
                    중도상환 수수료:{" "}
                    {(
                      (repaymentAmount * data.data.earlypayFee) /
                      100
                    ).toLocaleString()}{" "}
                    원
                  </p>
                  <p className="text-sm font-bold">
                    실제 납부 금액: {calculateTotalAmount().toLocaleString()} 원
                  </p>
                  {isExceedBalance() && (
                    <p className="text-sm text-red-500">
                      보유 금액이 부족합니다.
                    </p>
                  )}
                </>
              )}
            </div>
            <div className="flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => setIsEarlyRepaymentOpen(false)}
              >
                취소
              </Button>
              <Button
                onClick={handleEarlyRepayment}
                disabled={
                  earlyRepaymentMutation.isPending ||
                  !repaymentAmount ||
                  isExceedBalance()
                }
              >
                {earlyRepaymentMutation.isPending ? "처리중..." : "확인"}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default BorrowDetail;
