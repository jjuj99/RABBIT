import { InfoRow } from "@/entities/common/ui/InfoRow";
import LoanInfo from "@/features/loan/ui/LoanInfo";
import { Separator } from "@/shared/ui/Separator";
import { ContractPeriod } from "@/entities/loan/ui/ContractPeriod";
import { Button } from "@/shared/ui/button";
import NFTEventList from "@/entities/common/ui/NFTEventList";
import NFTEventListMobile from "@/entities/common/ui/NFTEventListMobile";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import { useQuery } from "@tanstack/react-query";
import { getLentDetailAPI } from "@/entities/loan/api/loanApi";
import { useNavigate, useParams } from "react-router";

const LentDetail = () => {
  const isDesktop = useMediaQuery("lg");

  const { contractId } = useParams();
  const navigate = useNavigate();

  const { data } = useQuery({
    queryKey: ["lentDetail", contractId],
    queryFn: () => getLentDetailAPI(contractId!),
    enabled: !!contractId,
  });

  if (!contractId) {
    navigate("/loan");
    return null;
  }

  if (!data?.data) {
    return <div>데이터가 없습니다.</div>;
  }

  return (
    <div className="flex flex-col gap-8">
      <h2 className="text-xl font-bold sm:text-2xl">채권 상세</h2>
      <div className="flex h-fit flex-col items-center gap-4 md:flex-row md:items-start">
        <img
          src={data.data.nftImage}
          className="w-full rounded-sm md:h-[350px] md:w-[350px] lg:h-[466px] lg:w-[466px]"
        />
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
                  value={`${data.data.earlypayFee}%`}
                />
                {data.data.earlypayFlag ? (
                  <Button variant="primary" size="sm" className="w-full">
                    중도 상환
                  </Button>
                ) : (
                  <div className="text-center text-sm text-gray-400">
                    중도 상환 불가능
                  </div>
                )}
              </div>
            </div>
            <Separator />
          </div>
        </div>
      </div>
      <ContractPeriod
        startDate={data.data.contractDt}
        endDate={data.data.matDt}
      />
      <h2 className="text-xl font-bold sm:text-2xl">이벤트 리스트</h2>
      <div className="w-full rounded-sm bg-gray-900">
        {isDesktop ? (
          <NFTEventList data={data.data.eventList} />
        ) : (
          <NFTEventListMobile data={data.data.eventList} />
        )}
      </div>
    </div>
  );
};

export default LentDetail;
