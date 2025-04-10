import { InfoRow } from "@/entities/common/ui/InfoRow";
import LoanInfo from "@/features/loan/ui/LoanInfo";
import { Separator } from "@/shared/ui/Separator";
import { ContractPeriod } from "@/entities/loan/ui/ContractPeriod";
import NFTEventList from "@/entities/common/ui/NFTEventList";
import NFTEventListMobile from "@/entities/common/ui/NFTEventListMobile";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getLentDetailAPI, getloanEventAPI } from "@/entities/loan/api/loanApi";
import { useNavigate, useParams } from "react-router";
import { useState, Suspense } from "react";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";

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

const LentDetail = () => {
  const [isImageLoaded, setIsImageLoaded] = useState(false);
  const isDesktop = useMediaQuery("lg");

  const { contractId } = useParams();
  const navigate = useNavigate();

  const { data } = useSuspenseQuery({
    queryKey: ["lentDetail", contractId],
    queryFn: () => getLentDetailAPI(contractId!),
  });

  console.log(data);

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
            crName={data.data.drName}
            crWallet={data.data.drWallet}
            la={data.data.la}
            totalAmount={data.data.totalAmount}
            repayType={data.data.repayType}
            ir={data.data.ir}
            dir={data.data.dir}
            defCnt={data.data.defCnt}
            contractDt={data.data.contractDt}
            pnStatus={data.data.pnStatus}
            earlypayFlag={data.data.earlypayFlag}
            earlypayFee={data.data.earlypayFee}
          />
          <div>
            <div className="flex h-full flex-col gap-2 lg:flex-row">
              <div className="flex h-full w-full flex-col justify-center gap-2 rounded-sm bg-gray-800 px-4 py-3">
                <InfoRow label="다음 상환 일자" value={data.data.nextMpDt} />
                <InfoRow
                  label="상환 금액"
                  value={data.data.nextAmount.toLocaleString()}
                />
              </div>
              <div className="flex h-full w-full flex-col justify-center gap-2 rounded-sm bg-gray-800 px-4 py-3">
                <InfoRow
                  label="기한 이익 상실"
                  value={`${data.data.accel}회 연체시`}
                />
                <InfoRow
                  label="기한 이익 상실 이자율"
                  value={`${data.data.accelDir}%`}
                />
              </div>
            </div>
            <Separator />
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
    </div>
  );
};

export default LentDetail;
