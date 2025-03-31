import { mockBorrowData } from "@/entities/loan/mocks/data";
import BorrowInfo from "@/entities/loan/ui/BorrowInfo";
import BorrowInfoMobile from "@/entities/loan/ui/BorrowInfoMobile";
import LoanSummaryCarousel from "@/features/loan/ui/LoanSummaryCarousel";
import LoanSummaryList from "@/features/loan/ui/LoanSummaryList";
import useMediaQuery from "@/shared/hooks/useMediaQuery";

const BorrowList = () => {
  const isDesktop = useMediaQuery("lg");

  if (!mockBorrowData || mockBorrowData.length === 0) {
    return (
      <div className="w-full overflow-hidden rounded-lg bg-gray-900 p-4">
        <div className="text-center text-base text-gray-400">
          대출 내역이 없습니다.
        </div>
      </div>
    );
  }

  return (
    <section className="flex flex-col gap-8">
      <div className="relative">
        {isDesktop ? <LoanSummaryList /> : <LoanSummaryCarousel />}
      </div>
      <h2 className="text-xl font-semibold">빌린 내역</h2>
      <div className="w-full overflow-hidden rounded-lg bg-gray-900">
        {isDesktop ? (
          <BorrowInfo data={mockBorrowData} />
        ) : (
          <BorrowInfoMobile data={mockBorrowData} />
        )}
      </div>
    </section>
  );
};

export default BorrowList;
