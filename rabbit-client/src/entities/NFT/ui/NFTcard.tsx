import { PNInfoListResponse } from "@/features/auction/types/response";
import { cn } from "@/shared/lib/utils";
import CountdownTimer from "@/shared/ui/CountdownTimer";
import { Link } from "react-router";

type NFTCardProps = {
  item: PNInfoListResponse;
};

export const NFTCard = ({ item }: NFTCardProps) => {
  return (
    <Link
      to={`/auction/${item.auctionId}`}
      className="group bg-black-glass border-white-glass shadow-glow flex h-fit w-[326px] flex-col items-center gap-3 rounded-lg border px-3 pt-4 pb-7 md:w-[300px] 2xl:w-[326px]"
    >
      <div className="relative h-full w-full rounded-sm">
        <NFTCardInfo item={item} />
        <img
          src={item.nftImageUrl}
          alt="NTF"
          className="opacity-100 transition-opacity duration-300 ease-in-out group-hover:opacity-0"
        />
      </div>

      <span className="flex w-full justify-center gap-2 text-[18px]">
        경매 종료까지
        <div className="w-[80px] justify-center">
          <CountdownTimer endDate={item.endDate} />
        </div>
      </span>
      <div className="bg-radial-accent flex w-full flex-col items-center gap-1 rounded-sm px-4 py-3">
        <span className="font-semibold">현재 입찰가</span>
        <div>
          <span
            className={cn(
              "text-brand-gradient font-partial",
              item.price.toString().length < 10 ? "text-xl" : "text-base",
            )}
          >
            {item.price.toLocaleString()}
          </span>
          <span className="text-brand-primary font-pixel text-sm">RAB</span>
        </div>
        <div className="font-bit mt-3 flex w-full justify-between text-sm">
          <div className="items-cen flex flex-col">
            <span>원금</span>
            <div>
              <span>{item.la.toLocaleString()}</span>
              <span className="text-brand-primary">₩</span>
            </div>
          </div>
          <div className="flex flex-col items-end">
            <span>이자율</span>
            <span>{item.ir}%</span>
          </div>
        </div>
      </div>
    </Link>
  );
};

interface NFTcardInfoProps {
  className?: string;
  item: PNInfoListResponse;
}

export const NFTCardInfo = ({ className, item }: NFTcardInfoProps) => {
  return (
    <div
      className={cn(
        "absolute flex h-full w-full flex-col justify-between px-6 py-4 opacity-0 transition-opacity duration-300 ease-in-out group-hover:opacity-100",
        className,
      )}
    >
      <div className="flex justify-between">
        <span>최초 등록일</span>
        <span>{new Date(item.createdAt).toLocaleDateString()}</span>
      </div>
      <div className="flex justify-between">
        <span>종류</span>
        <span>{item.repayType}</span>
      </div>
      <div className="flex justify-between">
        <span>만기 수취액</span>
        <div>
          <span>{item.totalAmount.toLocaleString()}</span>RAB
        </div>
      </div>
      <div className="flex justify-between">
        <span>만기일</span>
        <span>{new Date(item.matDt).toLocaleDateString()}</span>
      </div>
      <div className="flex justify-between">
        <span>연체 이자율</span>
        <div>
          <span>{item.dir}</span>%
        </div>
      </div>
      <div className="flex justify-between">
        <span>중도 상환 수수료</span>
        <span>{item.earlypayFee}%</span>
      </div>
      <div className="flex justify-between">
        <span>신용 점수</span>
        <span>{item.creditScore}</span>
      </div>
      <div className="flex justify-between">
        <span>연체</span>
        <span>{item.defCnt}</span>
      </div>
    </div>
  );
};
