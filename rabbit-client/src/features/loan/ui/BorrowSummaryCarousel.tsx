import LoanSummary from "@/entities/loan/ui/LoanSummary";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselDots,
  type CarouselApi,
} from "@/shared/ui/carousel";
import { useEffect, useState } from "react";
import { BorrowSummaryResponse } from "@/entities/loan/types/response";

interface BorrowSummaryCarouselProps {
  summary?: BorrowSummaryResponse;
}

const BorrowSummaryCarousel = ({
  summary = {
    totalOutgoingLa: 0,
    monthlyOutgoingLa: 0,
    nextOutgoingDt: "없음",
  },
}: BorrowSummaryCarouselProps) => {
  const [api, setApi] = useState<CarouselApi | null>(null);

  useEffect(() => {
    if (!api) return;

    const interval = setInterval(() => {
      const currentIndex = api.selectedScrollSnap();
      if (currentIndex === 2) {
        api.scrollTo(0);
      } else {
        api.scrollNext();
      }
    }, 3000);

    return () => clearInterval(interval);
  }, [api]);

  return (
    <Carousel
      opts={{
        align: "start",
        dragFree: true,
        containScroll: false,
      }}
      setApi={setApi}
    >
      <CarouselContent className="-ml-2">
        <CarouselItem className="basis-[85%] pl-2">
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
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="월 상환 예정 금액"
            mainContent={`${summary.monthlyOutgoingLa.toLocaleString()} RAB`}
            subContent="월 지출"
            className="w-full"
          />
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="다음 상환일"
            mainContent={summary.nextOutgoingDt}
            subContent="가장 가까운 상환일"
            className="w-full"
          />
        </CarouselItem>
      </CarouselContent>
      <CarouselDots className="mt-4" />
    </Carousel>
  );
};

export default BorrowSummaryCarousel;
