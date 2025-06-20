import LentInfo from "@/entities/loan/ui/LentInfo";
import LentInfoMobile from "@/entities/loan/ui/LentInfoMobile";
import LoanSummaryCarousel from "@/features/loan/ui/LentSummaryCarousel";
import LoanSummaryList from "@/features/loan/ui/LentSummaryList";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { getLentListAPI, getLentSummaryAPI } from "@/entities/loan/api/loanApi";
import { useState, useEffect } from "react";
import { Button } from "@/shared/ui/button";
import { useNavigate } from "react-router";
import { Skeleton } from "@/shared/ui/skeleton";

// 스켈레톤 UI 컴포넌트
const LoanSummarySkeleton = ({ isDesktop }: { isDesktop: boolean }) => {
  if (isDesktop) {
    return (
      <div className="grid grid-cols-3 gap-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="rounded-lg bg-gray-800 p-4">
            <Skeleton className="mb-2 h-4 w-1/3" />
            <Skeleton className="mb-4 h-6 w-1/2" />
            <Skeleton className="h-12 w-full" />
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="flex overflow-hidden">
      {[1, 2, 3].map((i) => (
        <div key={i} className="mr-4 min-w-[280px] rounded-lg bg-gray-800 p-4">
          <Skeleton className="mb-2 h-4 w-1/3" />
          <Skeleton className="mb-4 h-6 w-1/2" />
          <Skeleton className="h-12 w-full" />
        </div>
      ))}
    </div>
  );
};

const LentList = () => {
  const isDesktop = useMediaQuery("lg");
  const [page, setPage] = useState(() => {
    const params = new URLSearchParams(window.location.search);
    return Number(params.get("page")) || 0;
  });

  const navigate = useNavigate();
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    params.set("page", page.toString());
    window.history.replaceState({}, "", `?${params.toString()}`);
  }, [page]);

  const { data: LentSummary, isLoading: LentSummaryLoading } = useQuery({
    queryKey: ["LentSummary"],
    queryFn: () => getLentSummaryAPI(),
  });

  const { data: LentList, isLoading: LentListLoading } = useSuspenseQuery({
    queryKey: ["LentList", page],
    queryFn: () => getLentListAPI({ pageNumber: page, pageSize: 10 }),
  });

  if (!LentList?.data || LentList.data.content.length === 0) {
    return (
      <div className="w-full overflow-hidden rounded-lg bg-gray-900 p-4">
        <div className="flex min-h-[300px] items-center justify-center text-center text-base text-gray-400">
          보유한 채권이 없습니다.
        </div>
      </div>
    );
  }

  if (LentListLoading) {
    return <div>Loading...</div>;
  }

  return (
    <section className="flex flex-col gap-8">
      <div className="relative">
        {LentSummaryLoading ? (
          <LoanSummarySkeleton isDesktop={isDesktop} />
        ) : isDesktop ? (
          <LoanSummaryList summary={LentSummary?.data} />
        ) : (
          <LoanSummaryCarousel summary={LentSummary?.data} />
        )}
      </div>

      <div className="flex flex-row items-center justify-between">
        <h2 className="text-xl font-semibold">빌려준 내역</h2>
        <Button
          variant="glass"
          className="w-fit"
          size="sm"
          onClick={() => {
            navigate("/auction/new");
          }}
        >
          <span>판매하기</span>
        </Button>
      </div>
      <div className="w-full overflow-hidden rounded-lg">
        {isDesktop ? (
          <LentInfo data={LentList.data} onPageChange={setPage} />
        ) : (
          <LentInfoMobile data={LentList.data} onPageChange={setPage} />
        )}
      </div>
    </section>
  );
};

export default LentList;
