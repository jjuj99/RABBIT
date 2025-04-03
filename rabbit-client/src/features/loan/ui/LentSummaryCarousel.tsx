import LoanSummary from "@/entities/loan/ui/LoanSummary";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselDots,
  type CarouselApi,
} from "@/shared/ui/carousel";
import { useEffect, useState } from "react";
import { LentSummaryResponse } from "@/entities/loan/types/response";

interface LentSummaryCarouselProps {
  summary?: LentSummaryResponse;
}

const LentSummaryCarousel = ({
  summary = {
    totalIncomingLa: 0,
    monthlyIncomingLa: 0,
    nextIncomingDt: "없음",
  },
}: LentSummaryCarouselProps) => {
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
            title="채권 총계"
            mainContent={
              summary.totalIncomingLa
                ? `${summary.totalIncomingLa.toLocaleString()} 원`
                : "0 원"
            }
            subContent="빌려준 금액"
            className="w-full"
          />
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="월 상환 예정 금액"
            mainContent={`${summary.monthlyIncomingLa.toLocaleString()} RAB`}
            subContent="월 수익"
            className="w-full"
          />
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="다음 상환일"
            mainContent={summary.nextIncomingDt}
            subContent="가장 가까운 상환일"
            className="w-full"
          />
        </CarouselItem>
      </CarouselContent>
      <CarouselDots className="mt-4" />
    </Carousel>
  );
};

export default LentSummaryCarousel;
