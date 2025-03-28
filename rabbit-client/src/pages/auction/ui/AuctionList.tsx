import { NFTCard } from "@/entities/NFT/ui/NFTcard";
import AuctionFilter from "@/features/auction/ui/AuctionFilter";
import { Sheet, SheetContent, SheetTrigger } from "@/shared/ui/sheet";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";
import { getAuctionListAPI } from "@/features/auction/api/auctionApi";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/shared/ui/button";
import { useNavigate } from "react-router";

const AuctionList = () => {
  const queryClient = useQueryClient();

  const min_price = useAuctionFilterStore((state) => state.min_price);
  const max_price = useAuctionFilterStore((state) => state.max_price);
  const max_ir = useAuctionFilterStore((state) => state.max_ir);
  const min_ir = useAuctionFilterStore((state) => state.min_ir);
  const max_rate = useAuctionFilterStore((state) => state.max_rate);
  const repay_type = useAuctionFilterStore((state) => state.repay_type);
  const mat_term = useAuctionFilterStore((state) => state.mat_term);
  const mat_start = useAuctionFilterStore((state) => state.mat_start);
  const mat_end = useAuctionFilterStore((state) => state.mat_end);
  const navigate = useNavigate();

  const { data: auctionData, isLoading } = useQuery({
    queryKey: [
      "auctionList",
      {
        min_price,
        max_price,
        max_ir,
        min_ir,
        max_rate,
        repay_type,
        mat_term,
        mat_start,
        mat_end,
      },
    ],
    queryFn: () =>
      getAuctionListAPI({
        min_price,
        max_price,
        max_ir,
        min_ir,
        max_rate,
        repay_type,
        mat_term,
        mat_start,
        mat_end,
      }),
  });

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
              <li>로딩중...</li>
            ) : !auctionData?.data?.content?.length ? (
              <li>진행중인 경매가 없습니다.</li>
            ) : (
              auctionData.data.content.map((item) => (
                <li key={item.auction_id}>
                  <NFTCard item={item} />
                </li>
              ))
            )}
          </ul>
        </div>
      </section>
    </div>
  );
};

export default AuctionList;
