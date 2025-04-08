import { repaymentTypeConfig } from "@/entities/contract/types/repaymentTypeConfig";
import { CreateContractRequest } from "@/entities/contract/types/request";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import { wonFormat } from "@/shared/utils/wonFormat";
import { useLocation, useNavigate } from "react-router";
interface LocationState {
  contractData: {
    data: CreateContractRequest;
  };
}
const ContractRequestSuccess = () => {
  const { state } = useLocation() as { state: LocationState };
  const { contractData } = state;
  console.log("state에 담긴 데이터", contractData);

  const navigate = useNavigate();

  return (
    <main className="flex h-full flex-col items-center justify-center gap-16 bg-gray-900 py-24">
      <img
        src="/images/svg/success.svg"
        alt="차용증 제안 전송 성공"
        className="w-[100px] md:w-[150px]"
      />
      <div className="text-center">
        <h2 className="pb-2 text-lg font-bold md:text-2xl">
          차용증 제안이 전송되었습니다.
        </h2>
        <div className="text-text-secondary text-sm md:text-lg">
          <p>대출자가 제안을 검토하고 승인하면 차용증이 자동으로 발행됩니다.</p>
          <p> 승인 완료 시, 등록된 이메일과 알림을 통해 안내해 드립니다.</p>
        </div>
      </div>
      <div className="flex flex-col gap-6 text-sm md:flex-row md:gap-8 md:text-base">
        <div className="flex flex-col gap-4">
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">채무자 : </span>
            <span className="flex">
              <span>{contractData.data.drName}</span>(
              <span className="max-w-[100px]">
                {truncateAddress(contractData.data.drWallet)}
              </span>
            </span>
            )
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">차용 금액 : </span>
            <span>{wonFormat(contractData.data.la)} RAB</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">대출 기간 : </span>
            <span>{contractData.data.lt}개월</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">상환일 : </span>
            <span>매월 {contractData.data.mpDt}일</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">중도 상환 이자율 : </span>
            <span>{contractData.data.dir}%</span>
          </div>
        </div>
        <div className="flex flex-col gap-4">
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">채권자 : </span>
            <span className="flex">
              <span>{contractData.data.crName}</span>(
              {truncateAddress(contractData.data.crWallet)})
            </span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">이자율 : </span>
            <span>{contractData.data.ir}%</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">상환 방식 : </span>
            <span>{repaymentTypeConfig[contractData.data.repayType]}</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">연체이자율 : </span>
            <span>{contractData.data.dir}%</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">차용증 양도 : </span>
            <span
              className={cn(
                contractData.data.pnTransFlag
                  ? "text-green-500"
                  : "text-red-500",
              )}
            >
              {contractData.data.pnTransFlag ? "동의" : "비동의"}
            </span>
          </div>
        </div>
      </div>
      <div className="flex gap-4">
        <Button
          className="w-[170px] font-bold"
          onClick={() => {
            navigate("/contract/sent");
          }}
          variant="primary"
          size="lg"
        >
          확인
        </Button>
      </div>
    </main>
  );
};

export default ContractRequestSuccess;
