import LoanSummary from "@/entities/loan/ui/LoanSummary";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselDots,
  type CarouselApi,
} from "@/shared/ui/carousel";
import { useEffect, useState } from "react";

const LoanSummaryCarousel = () => {
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
            mainContent="100,000,000원"
            subContent="대출 요약"
            className="w-full"
          />
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="월 상환 예정 금액"
            mainContent="10,000,000원"
            subContent="대출 요약"
            className="w-full"
          />
        </CarouselItem>
        <CarouselItem className="basis-[85%] pl-2">
          <LoanSummary
            title="다음 상환일"
            mainContent="2025.03.30"
            subContent="대출 요약"
            className="w-full"
          />
        </CarouselItem>
      </CarouselContent>
      <CarouselDots className="mt-4" />
    </Carousel>
  );
};

export default LoanSummaryCarousel;
