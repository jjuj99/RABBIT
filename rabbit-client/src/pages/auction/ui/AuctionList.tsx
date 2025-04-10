import { NFTCard } from "@/entities/NFT/ui/NFTcard";
import AuctionFilter from "@/features/auction/ui/AuctionFilter";
import { Sheet, SheetContent, SheetTrigger } from "@/shared/ui/sheet";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { getAuctionListAPI } from "@/features/auction/api/auctionApi";
import { AuctionListRequest } from "@/features/auction/types/request";
import { useInfiniteQuery, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/shared/ui/button";
import { useNavigate } from "react-router";
import { useEffect, useRef } from "react";
import { cn } from "@/shared/lib/utils";

const AuctionList = () => {
  const queryClient = useQueryClient();
  const loadMoreRef = useRef<HTMLDivElement | null>(null);

  const minPrice = useAuctionFilterStore((state) => state.minPrice);
  const maxPrice = useAuctionFilterStore((state) => state.maxPrice);
  const maxIr = useAuctionFilterStore((state) => state.maxIr);
  const minIr = useAuctionFilterStore((state) => state.minIr);
  const maxRate = useAuctionFilterStore((state) => state.maxRate);
  const repayType = useAuctionFilterStore((state) => state.repayType);
  const matTerm = useAuctionFilterStore((state) => state.matTerm);
  const matStart = useAuctionFilterStore((state) => state.matStart);
  const matEnd = useAuctionFilterStore((state) => state.matEnd);
  const navigate = useNavigate();

  const {
    data: auctionData,
    isLoading,
    fetchNextPage,
    hasNextPage,
  } = useInfiniteQuery({
    queryKey: [
      "auctionList",
      {
        minPrice,
        maxPrice,
        maxIr,
        minIr,
        maxRate,
        repayType,
        matTerm,
        matStart,
        matEnd,
      },
    ],
    queryFn: ({ pageParam }) => {
      const params: AuctionListRequest = {};
      params.pageSize = 4;
      params.pageNumber = pageParam;
      if (minPrice) params.minPrice = Number(minPrice);
      if (maxPrice) params.maxPrice = Number(maxPrice);
      if (maxIr) params.maxIr = Number(maxIr);
      if (minIr) params.minIr = Number(minIr);
      if (maxRate) params.maxRate = Number(maxRate);
      if (repayType && repayType.length > 0) params.repayType = repayType;
      if (matTerm) params.matTerm = Number(matTerm);
      if (matStart) params.matStart = matStart;
      if (matEnd) params.matEnd = matEnd;

      return getAuctionListAPI(params);
    },
    getNextPageParam: (lastPage) => {
      if (lastPage && lastPage.hasNext) {
        return lastPage.pageNumber + 1;
      }
      return undefined;
    },
    initialPageParam: 0,
  });
  console.log("auctionData", auctionData);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage) {
          console.log("hasNextPage", hasNextPage);

          console.log("fetchNextPage");
          fetchNextPage();
        }
      },
      { threshold: 1.0 },
    );

    if (loadMoreRef.current) {
      observer.observe(loadMoreRef.current);
    }

    return () => {
      if (loadMoreRef.current) {
        observer.unobserve(loadMoreRef.current);
      }
    };
  }, [fetchNextPage, hasNextPage]);

  const handleFilterChange = () => {
    queryClient.invalidateQueries({ queryKey: ["auctionList"] });
  };

  return (
    <div className="flex w-full flex-row gap-9">
      <div className="sticky hidden md:block">
        <AuctionFilter onFilterChange={handleFilterChange} />
      </div>
      <section className="flex flex-1 flex-col gap-6 sm:gap-6">
        <div className="flex flex-col gap-4">
          <div className="flex h-fit flex-row gap-2 sm:justify-between md:gap-0">
            <Sheet>
              <SheetTrigger className="block h-full items-center justify-center rounded-sm border border-white bg-gray-900 px-2 whitespace-nowrap md:hidden">
                옵션
              </SheetTrigger>
              <SheetContent side="right" className="w-fit">
                <AuctionFilter
                  className="h-full"
                  onFilterChange={handleFilterChange}
                />
              </SheetContent>
            </Sheet>
            <div className="flex h-fit w-full flex-row rounded-sm border border-white bg-gray-900 px-3 py-1 md:px-6 md:py-3">
              <h2 className="font-dunggeunmo w-fit text-lg md:text-2xl xl:items-start">
                <span className="text-brand-primary">경매 진행중인 </span>
                <span>차용증</span>
              </h2>
            </div>
          </div>
          <div className="flex justify-end">
            <Button
              variant="glass"
              className="w-fit"
              onClick={() => {
                navigate("/auction/new");
              }}
            >
              <span>+ 경매 생성</span>
            </Button>
          </div>
        </div>
        <div className="flex w-full items-center justify-center xl:items-start xl:justify-start">
          <ul className="grid gap-10 lg:grid-cols-2 xl:grid-cols-3">
            {isLoading ? (
              <div className="loader-sprite"></div>
            ) : auctionData && auctionData.pages.length > 0 ? (
              auctionData.pages.map((page) =>
                page.content.map((item) => (
                  <li key={item.auctionId}>
                    <NFTCard item={item} />
                  </li>
                )),
              )
            ) : (
              <li>진행중인 경매가 없습니다.</li>
            )}
          </ul>
          <div
            ref={loadMoreRef}
            className={cn(isLoading ? "loader-sprite" : "h-10")}
          ></div>
        </div>
      </section>
    </div>
  );
};

export default AuctionList;
