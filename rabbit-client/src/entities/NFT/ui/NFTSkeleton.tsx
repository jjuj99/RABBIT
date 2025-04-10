import { cn } from "@/shared/lib/utils";
import { Separator } from "@/shared/ui/Separator";

interface NFTSkeletonProps {
  className?: string;
}

export const NFTSkeleton = ({ className }: NFTSkeletonProps) => {
  return (
    <div
      className={cn(
        "bg-black-glass border-white-glass shadow-glow flex h-fit w-full max-w-[300px] min-w-[270px] flex-col items-center gap-4 rounded-lg border px-3 pt-4 pb-7",
        className,
      )}
    >
      <div className="text-brand-primary font-bit flex w-full items-center justify-center text-base">
        <div className="h-5 w-32 animate-pulse rounded-md bg-gray-700"></div>
      </div>
      <div className="relative h-[192px] w-full">
        <div className="flex justify-center">
          {/* 이미지 스켈레톤 */}
          <div className="h-[192px] w-[192px] animate-pulse rounded-md bg-gray-700"></div>
        </div>
      </div>

      <div className="bg-radial-accent flex h-fit w-full flex-col items-center gap-2 rounded-sm px-6 py-3">
        <div className="flex w-full justify-between">
          <span>채권자</span>
          <div className="h-4 w-24 animate-pulse rounded-md bg-gray-700"></div>
        </div>
        <div className="flex w-full justify-between">
          <span>만기일</span>
          <div className="h-4 w-24 animate-pulse rounded-md bg-gray-700"></div>
        </div>
        <div className="flex w-full justify-between">
          <span>원금</span>
          <div className="h-4 w-24 animate-pulse rounded-md bg-gray-700"></div>
        </div>
        <div className="flex w-full justify-between">
          <span>이자율</span>
          <div className="h-4 w-10 animate-pulse rounded-md bg-gray-700"></div>
        </div>
        <Separator />
        <div className="flex w-full justify-between">
          <span>만기 총 수취액</span>
          <div className="h-4 w-24 animate-pulse rounded-md bg-gray-700"></div>
        </div>
      </div>
    </div>
  );
};
