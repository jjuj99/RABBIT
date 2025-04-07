import { Button } from "@/shared/ui/button";
import { useLocation, useNavigate } from "react-router";

const ContractRequestDenied = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  console.log(state);
  return (
    <main className="flex h-full flex-col items-center justify-center gap-16 bg-gray-900 py-24">
      <img
        src="/images/svg/denied.svg"
        alt="차용증 제안 거절"
        className="w-[100px] md:w-[150px]"
      />
      <div className="text-center">
        <h2 className="pb-2 text-lg font-bold md:text-2xl">
          차용증 제안이 거절되었습니다.
        </h2>
        <div className="text-text-secondary text-sm md:text-lg">
          <p>대출자가 제안을 검토하고 거절하였습니다.</p>
          <p>새로운 차용증을 작성하시려면 다시 시도해주세요.</p>
        </div>
      </div>
      <div className="flex flex-col gap-6 text-sm md:flex-row md:gap-8 md:text-base">
        <div className="flex flex-col gap-4">
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">채무자 : </span>
            <span className="flex"></span>
            <span>박성문</span>(
            <span className="max-w-[100px] truncate">{`010-1234-5678`}</span>)
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">차용 금액 : </span>
            <span>100,000,000 RAB</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">대출 기간 : </span>
            <span>12개월</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">상환일 : </span>
            <span>매월 15일</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">중도 상환 이자율 : </span>
            <span>10%</span>
          </div>
        </div>
        <div className="flex flex-col gap-4">
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">채권자 : </span>
            <span className="flex"></span>
            <span>박성문</span>(
            <span className="max-w-[100px] truncate">{`010-1234-5678`}</span>)
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">이자율 : </span>
            <span>10%</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">상환 방식 : </span>
            <span>원리금 균등 상환</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">연체이자율 : </span>
            <span>10%</span>
          </div>
          <div className="flex w-[220px] justify-between">
            <span className="text-text-secondary">차용증 양도 : </span>
            <span>동의</span>
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

export default ContractRequestDenied;
