import LoanSummary from "@/entities/loan/ui/LoanSummary";

const LoanSummaryList = () => {
  return (
    <div className="flex flex-row justify-center gap-4">
      <LoanSummary
        title="채무 총계"
        mainContent="100,000,000원"
        subContent="대출 요약"
        className="w-full"
      />
      <LoanSummary
        title="월 상환 예정 금액"
        mainContent="10,000,000원"
        subContent="대출 요약"
        className="w-full"
      />
      <LoanSummary
        title="다음 상환일"
        mainContent="2025.03.30"
        subContent="대출 요약"
        className="w-full"
      />
    </div>
  );
};

export default LoanSummaryList;
