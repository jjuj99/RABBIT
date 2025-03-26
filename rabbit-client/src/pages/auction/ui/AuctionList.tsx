import { NFTCard } from "@/entities/NFT/NFTcard";
import AuctionFilter from "@/features/auction/ui/AuctionFilter";
import { Sheet, SheetContent, SheetTrigger } from "@/shared/ui/sheet";

const AuctionList = () => {
  return (
    <div className="flex w-full flex-row gap-9">
      <div className="hidden md:block">
        <AuctionFilter />
      </div>

      <section className="flex flex-1 flex-col gap-6 sm:gap-9">
        <div className="flex h-fit flex-row gap-2 sm:justify-between md:gap-0">
          <div className="h-fit w-full rounded-sm border border-white bg-gray-900 px-3 py-1 md:px-6 md:py-3">
            <h2 className="font-dunggeunmo w-fit text-lg md:text-2xl xl:items-start">
              <span className="text-brand-primary">경매 진행중인</span>{" "}
              <span>차용증</span>
            </h2>
          </div>
          <Sheet>
            <SheetTrigger className="block h-full items-center justify-center rounded-sm border border-white bg-gray-900 px-2 whitespace-nowrap md:hidden">
              옵션
            </SheetTrigger>
            <SheetContent side="left" className="w-fit">
              <AuctionFilter className="h-full" />
            </SheetContent>
          </Sheet>
        </div>
        <div className="flex w-full items-center justify-center xl:items-start xl:justify-start">
          <div className="grid gap-6 lg:grid-cols-2 2xl:grid-cols-3">
            <NFTCard />
            <NFTCard />
            <NFTCard />
          </div>
        </div>
      </section>
    </div>
  );
};

export default AuctionList;
