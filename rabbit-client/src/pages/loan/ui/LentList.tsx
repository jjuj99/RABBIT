import { mockLentData } from "@/entities/loan/mocks/data";
import LentInfo from "@/entities/loan/ui/LentInfo";
import LentInfoMobile from "@/entities/loan/ui/LentInfoMobile";
import LoanSummaryCarousel from "@/features/loan/ui/LoanSummaryCarousel";
import LoanSummaryList from "@/features/loan/ui/LoanSummaryList";
import useMediaQuery from "@/shared/hooks/useMediaQuery";

const LentList = () => {
  const isDesktop = useMediaQuery("lg");

  if (!mockLentData || mockLentData.length === 0) {
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
      <h2 className="text-xl font-semibold">빌려준 내역</h2>
      <div className="w-full overflow-hidden rounded-lg bg-gray-900">
        {isDesktop ? (
          <LentInfo data={mockLentData} />
        ) : (
          <LentInfoMobile data={mockLentData} />
        )}
      </div>
    </section>
  );
};

export default LentList;
