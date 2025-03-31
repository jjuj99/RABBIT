import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { useState } from "react";

const ContractFinalReview = () => {
  const [agreementText, setAgreementText] = useState("");
  const CONFIRM_AGREEMENT_TEXT =
    "계약 내용을 숙지하였으며 자금 전송에 동의합니다.";
  return (
    <main className="flex h-full flex-col items-center justify-center gap-16 bg-gray-900 py-24">
      <img
        src="/images/svg/warning.svg"
        alt="계약 승인 및 자금 전송 주의"
        className="w-[100px] md:w-[150px]"
      />
      <div className="text-center">
        <h2 className="pb-2 text-lg font-bold md:text-2xl">
          계약 승인 및 자금 전송
        </h2>
        <div className="text-text-secondary text-sm md:text-lg">
          <p>이 작업은 블록체인에 기록되며 되돌릴 수 없습니다.</p>
          <p>
            <span className="text-brand-primary">확인</span> 버튼을 누르면
            스마트 컨트랙트가 실행되고 자금이 전송됩니다.
          </p>
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
      <div className="px-1 text-sm md:text-lg">
        <p>
          본 계약의 조건과 내용을 충분히 검토하였으며, 자금 전송에 대한 법적
          책임을 이해하고 있습니다.
        </p>
        <p>정확한 동의를 확인하기 위해 아래 문구를 입력해 주시기 바랍니다.</p>
        <p className="text-brand-primary mt-2 rounded-md bg-gray-800 px-3 py-1 text-sm">
          {CONFIRM_AGREEMENT_TEXT}
        </p>
        <Input
          type="text"
          className="mt-2 text-sm md:text-base"
          placeholder={CONFIRM_AGREEMENT_TEXT}
          onChange={(e) => setAgreementText(e.target.value)}
          value={agreementText}
        />
      </div>
      <div className="flex gap-4">
        <Button
          className="w-[170px] font-bold"
          variant="primary"
          size="lg"
          disabled={agreementText !== CONFIRM_AGREEMENT_TEXT}
        >
          확인
        </Button>
        <Button className="w-[170px] font-bold" variant="secondary" size="lg">
          제안 취소
        </Button>
      </div>
    </main>
  );
};

export default ContractFinalReview;
