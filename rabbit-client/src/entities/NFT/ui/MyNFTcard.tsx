import { AvailableAuctionsResponse } from "@/features/auction/types/response";
import { cn } from "@/shared/lib/utils";
import { Separator } from "@/shared/ui/Separator";
import dateFormat from "@/shared/utils/dateFormat";

type NFTCardProps = {
  item: AvailableAuctionsResponse;
};

export const MyNFTcard = ({ item }: NFTCardProps) => {
  return (
    <div className="group bg-black-glass border-white-glass shadow-glow w- fit flex h-fit w-[300px] flex-col items-center gap-4 rounded-lg border px-3 pt-4 pb-7">
      <div className="text-brand-primary font-bit flex w-full items-center justify-center text-base">
        <span>RABBIT#{item.tokenId}</span>
      </div>
      <div className="relative h-[192px] w-full">
        <MyNFTCardInfo item={item} />
        <div className="flex justify-center">
          <img
            src="/images/NFT.png"
            alt="NTF"
            className="h-[192px] w-[192px] opacity-100 transition-opacity duration-300 ease-in-out group-hover:opacity-0"
          />
        </div>
      </div>

      <div className="bg-radial-accent flex h-fit w-full flex-col items-center gap-2 rounded-sm px-6 py-3">
        <div className="flex w-full justify-between">
          <span>채권자</span>
          <span>{item.crName}</span>
        </div>
        <div className="flex w-full justify-between">
          <span>만기일</span>
          <span>{dateFormat(item.matDt)}</span>
        </div>
        <div className="flex w-full justify-between">
          <span>원금</span>
          <span>{item.la.toLocaleString()}원</span>
        </div>
        <div className="flex w-full justify-between">
          <span>이자율</span>
          <span>{item.ir}%</span>
        </div>
        <Separator />
        <div className="flex w-full justify-between">
          <span> 만기 총 수취액</span>
          <span>{item.totalAmount.toLocaleString()}원</span>
        </div>
      </div>
    </div>
  );
};

interface NFTcardInfoProps {
  className?: string;
  item: AvailableAuctionsResponse;
}

export const MyNFTCardInfo = ({ className, item }: NFTcardInfoProps) => {
  return (
    <div
      className={cn(
        "absolute flex h-full w-full flex-col justify-between px-6 py-4 opacity-0 transition-opacity duration-300 ease-in-out group-hover:opacity-100",
        className,
      )}
    >
      <div className="flex justify-between">
        <span>종류</span>
        <span>{item.repayType}</span>
      </div>

      <div className="flex justify-between">
        <span>연체 이자율</span>
        <div>
          <span>{item.dir}</span>%
        </div>
      </div>
      <div className="flex justify-between">
        <span>{item.earlypayFlag ? `중도 상환 수수료` : "중도 상환"}</span>
        <span>{item.earlypayFlag ? `${item.earlypayFee}%` : "불가"}</span>
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
