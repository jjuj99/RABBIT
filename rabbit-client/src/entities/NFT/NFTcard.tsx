import { cn } from "@/shared/lib/utils";
import { Link } from "react-router";

export const NFTCard = () => {
  return (
    <Link
      to={"#"}
      className="group bg-black-glass border-white-glass shadow-glow flex h-fit w-[326px] flex-col items-center gap-3 rounded-lg border px-3 pt-4 pb-7"
    >
      <div className="relative h-[302px] w-[302px] rounded-sm">
        <NFTCardInfo />
        <img
          src="/images/NFT.png"
          alt="NTF"
          className="opacity-100 transition-opacity duration-300 ease-in-out group-hover:opacity-0"
        />
      </div>
      <span className="text-[18px]">경매 종료까지 15 : 30 : 05</span>
      <div className="bg-radial-accent flex w-full flex-col items-center rounded-sm px-6 py-3">
        <span className="font-semibold">현재 입찰가</span>
        <div>
          <span className="text-brand-gradient font-partial text-xl">
            10,005
          </span>
          <span className="text-brand-primary font-pixel text-sm">RAB</span>
        </div>
        <div className="font-bit mt-3 flex w-full justify-between text-xs">
          <div>
            <span>원금 11,115,000</span>
            <span className="text-brand-primary font-pixel">RAB</span>
          </div>
          <div>
            <span>이자율</span>
            <span>113%</span>
          </div>
        </div>
      </div>
    </Link>
  );
};

interface NFTcardInfoProps {
  className?: string;
}

export const NFTCardInfo = ({ className }: NFTcardInfoProps) => {
  return (
    <div
      className={cn(
        "absolute flex h-[302px] w-[302px] flex-col justify-between px-6 py-4 opacity-0 transition-opacity duration-300 ease-in-out group-hover:opacity-100",
        className,
      )}
    >
      <div className="flex justify-between">
        <span>최초 등록일</span>
        <span>2023년 12월 13일</span>
      </div>
      <div className="flex justify-between">
        <span>종류</span>
        <span>원리금 균등 상환</span>
      </div>
      <div className="flex justify-between">
        <span>만기 수취액</span>
        <div>
          <span>11,115,000</span>RAB
        </div>
      </div>
      <div className="flex justify-between">
        <span>만기일</span>
        <span>2025년 12월 13일</span>
      </div>
      <div className="flex justify-between">
        <span>연체 이자율</span>
        <div>
          <span>5</span>%
        </div>
      </div>
      <div className="flex justify-between">
        <span>중도 상환 수수료</span>
        <span>불가능</span>
      </div>
      <div className="flex justify-between">
        <span>신용 점수</span>
        <span>980</span>
      </div>
      <div className="flex justify-between">
        <span>연체</span>
        <span>0</span>
      </div>
    </div>
  );
};
