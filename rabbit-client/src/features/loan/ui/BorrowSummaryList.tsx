import { BorrowSummaryResponse } from "@/entities/loan/types/response";
import LoanSummary from "@/entities/loan/ui/LoanSummary";

interface BorrowSummaryListProps {
  summary?: BorrowSummaryResponse;
}

const BorrowSummaryList = ({
  summary = {
    totalOutgoingLa: 0,
    monthlyOutgoingLa: 0,
    nextOutgoingDt: "없음",
  },
}: BorrowSummaryListProps) => {
  return (
    <div className="flex flex-row justify-center gap-4">
      <LoanSummary
        title="채무 총계"
        mainContent={
          summary.totalOutgoingLa
            ? `${summary.totalOutgoingLa.toLocaleString()} 원`
            : "0 원"
        }
        subContent="빌린 금액"
        className="w-full"
      />
      <LoanSummary
        title="월 상환 예정 금액"
        mainContent={
          summary.monthlyOutgoingLa
            ? `${summary.monthlyOutgoingLa.toLocaleString()} RAB`
            : "0 RAB"
        }
        subContent="월 납부"
        className="w-full"
      />
      <LoanSummary
        title="다음 상환일"
        mainContent={summary.nextOutgoingDt}
        subContent="가장 가까운 상환일"
        className="w-full"
      />
    </div>
  );
};

export default BorrowSummaryList;
