import { Circle, Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { LentListResponse } from "../types/response";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/shared/ui/pagination";
import { useNavigate } from "react-router";

interface LentInfoProps {
  data?: LentListResponse;
  onPageChange?: (page: number) => void;
}

const LentInfo = ({ data, onPageChange }: LentInfoProps) => {
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
    <div>
      <table className="w-full table-fixed rounded-sm bg-gray-900">
        <thead>
          <tr className="border-b border-gray-600">
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              NFT
            </th>
            <th className="w-[60px] px-4 py-3 text-center text-base font-medium text-gray-200">
              채무자
            </th>
            <th className="w-[80px] px-4 py-3 text-center text-base font-medium text-gray-200">
              지갑 주소
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              금액
            </th>
            <th className="w-[60px] px-4 py-3 text-center text-base font-medium text-gray-200">
              이자율
            </th>
            <th className="w-[90px] px-4 py-3 text-center text-base font-medium text-gray-200">
              만기일
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              상태
            </th>
          </tr>
        </thead>
        <tbody>
          {data.content.map((item) => (
            <tr
              key={item.tokenId}
              className="cursor-pointer border-b border-gray-800 transition-colors hover:bg-gray-800"
              onClick={() => navigate(`/loan/lent/${item.contractId}`)}
            >
              <td className="px-4 py-3">
                <div className="flex flex-col items-center gap-2">
                  <div className="flex flex-wrap items-center justify-center">
                    <span className="font-bit text-base font-medium text-white">
                      RABBIT
                    </span>
                    <span className="font-bit text-base font-medium text-white">
                      #{item.tokenId}
                    </span>
                  </div>
                  <img
                    src={item.nftImage}
                    alt="NFT"
                    className="h-[100px] w-[100px] rounded-lg object-cover"
                  />
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="flex items-center justify-center gap-2 text-base font-medium">
                  {item.drName}
                </div>
              </td>
              <td className="px-4 py-3 text-center text-base text-white">
                <div
                  className="hover:text-brand-primary mx-auto max-w-[180px] cursor-pointer truncate"
                  onClick={(e) => {
                    e.stopPropagation();
                    setSelectedWallet(item.drWallet);
                    setIsOpen(true);
                  }}
                >
                  {item.drWallet || "-"}
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="text-base text-white">
                  {item.la.toLocaleString()}₩
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="mx-auto w-fit text-base text-white">
                  {item.ir}%
                </div>
              </td>
              <td className="px-4 py-3 text-center">
                <div className="flex flex-col gap-1">
                  <div className="text-base text-white">{item.matDt}</div>
                  <div className="text-sm text-gray-300">
                    {item.remainTerms}일 남음
                  </div>
                </div>
              </td>
              <td className="px-4 py-3">
                <div className="flex flex-col items-center justify-center gap-1">
                  <div className="flex items-center justify-center gap-1">
                    <Circle
                      className={
                        item.pnStatus === "연체"
                          ? "fill-fail text-fail"
                          : "text-brand-primary fill-brand-primary"
                      }
                      width={8}
                      height={8}
                    />
                    <div>{item.pnStatus}</div>
                  </div>
                  <div className="flex flex-wrap justify-center gap-1 text-sm text-gray-200">
                    <span className="text-gray-300">다음 납부일 </span>
                    <span>{item.nextMpDt} </span>
                  </div>
                  {item.pnStatus === "연체" && item.aoi && item.aoiDays && (
                    <div className="text-center text-sm text-red-500">
                      연체금액: {item.aoi.toLocaleString()}₩
                      <br />
                      연체일수: {item.aoiDays}일
                    </div>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="bg-gray-900 text-white">
          <DialogTitle className="sr-only">지갑 주소 복사</DialogTitle>
          <div className="flex flex-col gap-4">
            <div className="text-lg font-medium">지갑 주소</div>
            <div className="flex items-center gap-2 rounded-lg bg-gray-800 p-3">
              <div className="flex-1 break-all">{selectedWallet}</div>
              <button
                onClick={() => handleCopy(selectedWallet)}
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
                  onClick={() => onPageChange?.(pageNum)}
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
    </div>
  );
};

export default LentInfo;
