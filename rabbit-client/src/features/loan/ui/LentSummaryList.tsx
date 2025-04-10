import { LentSummaryResponse } from "@/entities/loan/types/response";
import LoanSummary from "@/entities/loan/ui/LoanSummary";

interface LentSummaryListProps {
  summary?: LentSummaryResponse;
}

const LentSummaryList = ({
  summary = {
    totalIncomingLa: 0,
    monthlyIncomingLa: 0,
    nextIncomingDt: "없음",
  },
}: LentSummaryListProps) => {
  return (
    <div className="flex flex-row justify-center gap-4">
      <LoanSummary
        title="채권 총계"
        mainContent={
          summary.totalIncomingLa
            ? `${summary.totalIncomingLa.toLocaleString()} 원`
            : "0 원"
        }
        subContent="빌려준 금액"
        className="w-full flex-1"
      />
      <LoanSummary
        title="월 상환 예정 금액"
        mainContent={`${summary.monthlyIncomingLa.toLocaleString()} RAB`}
        subContent="월 수익"
        className="w-full flex-1"
      />
      <LoanSummary
        title="다음 상환일"
        mainContent={summary.nextIncomingDt}
        subContent="가장 가까운 상환일"
        className="w-full flex-1"
      />
    </div>
  );
};

export default LentSummaryList;
