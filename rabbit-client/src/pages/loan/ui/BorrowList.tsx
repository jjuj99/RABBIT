import {
  getBorrowListAPI,
  getBorrowSummaryAPI,
} from "@/entities/loan/api/loanApi";
import BorrowInfo from "@/entities/loan/ui/BorrowInfo";
import BorrowInfoMobile from "@/entities/loan/ui/BorrowInfoMobile";
import BorrowSummaryCarousel from "@/features/loan/ui/BorrowSummaryCarousel";
import BorrowSummaryList from "@/features/loan/ui/BorrowSummaryList";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { useState, useEffect } from "react";

const BorrowList = () => {
  const isDesktop = useMediaQuery("lg");
  const [page, setPage] = useState(() => {
    const params = new URLSearchParams(window.location.search);
    return Number(params.get("page")) || 0;
  });

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", page.toString());
    window.history.replaceState({}, "", `?${params.toString()}`);
  }, [page]);

  const { data: BorrowSummary } = useQuery({
    queryKey: ["BorrowSummary"],
    queryFn: () => getBorrowSummaryAPI(),
  });

  const { data: BorrowList, isLoading: BorrowListLoading } = useSuspenseQuery({
    queryKey: ["BorrowList", page],
    queryFn: () => getBorrowListAPI({ pageNumber: page, pageSize: 10 }),
  });

  if (!BorrowList?.data || BorrowList.data.content.length === 0) {
    return (
      <div className="w-full overflow-hidden rounded-lg bg-gray-900 p-4">
        <div className="text-center text-base text-gray-400">
          대출 내역이 없습니다.
        </div>
      </div>
    );
  }

  if (BorrowListLoading) {
    return <div>Loading...</div>;
  }

  return (
    <section className="flex flex-col gap-8">
      <div className="relative">
        {isDesktop ? (
          <BorrowSummaryList summary={BorrowSummary?.data} />
        ) : (
          <BorrowSummaryCarousel summary={BorrowSummary?.data} />
        )}
      </div>
      <h2 className="text-xl font-semibold">빌린 내역</h2>
      <div className="w-full overflow-hidden rounded-lg">
        {isDesktop ? (
          <BorrowInfo data={BorrowList.data} onPageChange={setPage} />
        ) : (
          <BorrowInfoMobile data={BorrowList.data} onPageChange={setPage} />
        )}
      </div>
    </section>
  );
};

export default BorrowList;
