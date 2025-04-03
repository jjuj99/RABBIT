import { Circle, Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { Separator } from "@/shared/ui/Separator";
import { BorrowListResponse } from "../types/response";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/shared/ui/pagination";
import { useNavigate } from "react-router";

interface BorrowInfoMobileProps {
  data?: BorrowListResponse;
  onPageChange?: (page: number) => void;
}

const BorrowInfoMobile = ({ data, onPageChange }: BorrowInfoMobileProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [selectedWallet, setSelectedWallet] = useState("");
  const navigate = useNavigate();

  const handleCopy = async (walletAddress: string) => {
    try {
      await navigator.clipboard.writeText(walletAddress);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    } catch (err) {
      console.error("복사 실패:", err);
    }
  };

  if (!data || data.content.length === 0) {
    return (
      <div className="flex h-[200px] items-center justify-center rounded-sm bg-gray-900">
        <div className="text-base text-gray-400">대출 정보가 없습니다.</div>
      </div>
    );
  }

  return (
    <>
      {data.content.map((item) => (
        <div
          key={item.tokenId}
          className="mb-4 cursor-pointer rounded-sm bg-gray-800 p-4 transition-colors hover:bg-gray-700"
          onClick={() => navigate(`/loan/borrow/${item.contractId}`)}
        >
          <div className="flex flex-row gap-4">
            <div className="flex w-fit flex-col gap-3">
              <div className="flex flex-col">
                <span className="font-bit text-xs font-medium text-white sm:text-base">
                  RABBIT
                </span>
                <span className="font-bit text-brand-primary text-xs font-medium sm:text-base">
                  #{item.tokenId}
                </span>
              </div>
              <div className="h-[64px] w-[64px] sm:h-[84px] sm:w-[84px]">
                <img
                  src={item.nftImage}
                  alt="NFT"
                  className="h-full w-full rounded-sm object-cover"
                />
              </div>
            </div>
            <div className="flex w-full flex-col gap-2">
              <div className="flex flex-row items-center justify-between">
                <span className="text-xs font-light text-white sm:text-base">
                  만기일 {item.matDt}
                </span>
                <div className="flex items-center gap-2">
                  <Circle
                    className={
                      item.pnStatus === "연체"
                        ? "fill-fail text-fail"
                        : "text-brand-primary fill-brand-primary"
                    }
                    width={8}
                    height={8}
                  />
                  <span className="text-xs font-medium sm:text-base">
                    {item.pnStatus}
                  </span>
                </div>
              </div>
              <Separator className="w-full" />
              <div className="flex flex-col gap-1">
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    채권자
                  </span>
                  <div className="flex flex-col items-end">
                    <span className="text-xs font-medium text-white sm:text-base">
                      {item.drName}
                    </span>
                    <span
                      className="hover:text-brand-primary cursor-pointer text-xs font-light text-gray-200 sm:text-sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        setSelectedWallet(item.drWallet);
                        setIsOpen(true);
                      }}
                    >
                      {item.drWallet
                        ? `${item.drWallet.slice(0, 6)}...${item.drWallet.slice(-4)}`
                        : "-"}
                    </span>
                  </div>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    대출금액
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.la.toLocaleString()}₩
                  </span>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    이자율
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.ir}%
                  </span>
                </div>
                <div className="flex flex-row justify-between">
                  <span className="text-xs font-light text-gray-100 sm:text-base">
                    다음 납부일
                  </span>
                  <span className="text-xs font-medium text-white sm:text-base">
                    {item.nextMpDt}
                  </span>
                </div>
                {item.pnStatus === "연체" && item.aoi && item.aoiDays && (
                  <div className="flex flex-row justify-between">
                    <span className="text-xs font-light text-red-500 sm:text-base">
                      연체금액
                    </span>
                    <span className="text-xs font-medium text-red-500 sm:text-base">
                      {item.aoi.toLocaleString()}₩
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      ))}

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="bg-gray-900 text-white">
          <DialogTitle className="sr-only">지갑 주소 복사</DialogTitle>
          <div className="flex flex-col gap-4">
            <div className="text-lg font-medium">지갑 주소</div>
            <div className="flex items-center gap-2 rounded-lg bg-gray-800 p-3">
              <div className="flex-1 break-all">{selectedWallet}</div>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleCopy(selectedWallet);
                }}
                className="flex items-center gap-1 rounded-lg bg-gray-700 px-3 py-1 hover:bg-gray-600"
              >
                {isCopied ? (
                  <Check className="text-brand-primary" size={16} />
                ) : (
                  <Copy size={16} />
                )}
                <span>{isCopied ? "복사됨" : "복사"}</span>
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      <Pagination className="mt-4">
        <PaginationContent>
          <PaginationItem>
            <PaginationPrevious
              onClick={() => onPageChange?.(data.pageNumber - 1)}
              className={
                data.pageNumber === 0 ? "pointer-events-none opacity-50" : ""
              }
            />
          </PaginationItem>
          {(() => {
            const startPage = Math.max(
              0,
              Math.min(data.pageNumber - 2, data.totalPages - 4),
            );
            const endPage = Math.min(data.totalPages - 1, startPage + 4);
            const pages = Array.from(
              { length: endPage - startPage + 1 },
              (_, i) => startPage + i,
            );

            return pages.map((pageNum) => (
              <PaginationItem key={pageNum}>
                <PaginationLink
                  onClick={(e) => {
                    e.stopPropagation();
                    onPageChange?.(pageNum);
                  }}
                  isActive={pageNum === data.pageNumber}
                >
                  {pageNum + 1}
                </PaginationLink>
              </PaginationItem>
            ));
          })()}
          <PaginationItem>
            <PaginationNext
              onClick={() => onPageChange?.(data.pageNumber + 1)}
              className={data.last ? "pointer-events-none opacity-50" : ""}
            />
          </PaginationItem>
        </PaginationContent>
      </Pagination>
    </>
  );
};

export default BorrowInfoMobile;
