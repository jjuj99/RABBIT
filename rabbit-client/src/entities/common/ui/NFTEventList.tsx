import { Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { NFTEvent } from "@/shared/type/NFTEventList";
import { ScrollArea } from "@/shared/ui/scroll-area";
import {
  ScrollAreaScrollbar,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";

interface NFTEventListProps {
  data?: NFTEvent[];
}

const NFTEventList = ({ data }: NFTEventListProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [selectedWallet, setSelectedWallet] = useState("");

  const handleCopy = async (walletAddress: string) => {
    try {
      await navigator.clipboard.writeText(walletAddress);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    } catch (err) {
      console.error("복사 실패:", err);
    }
  };

  if (!data || data.length === 0) {
    return (
      <div className="flex h-[100px] items-center justify-center rounded-sm bg-gray-800">
        <div className="text-base text-gray-100">이벤트 정보가 없습니다.</div>
      </div>
    );
  }

  return (
    <div>
      <table className="w-full table-fixed bg-gray-900">
        <thead>
          <tr className="border-b border-gray-600">
            <th className="w-[50px] px-4 py-3 text-center text-base font-medium text-gray-200">
              종류
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              금액
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              전송인
            </th>
            <th className="w-[120px] px-4 py-3 text-center text-base font-medium text-gray-200">
              수신인
            </th>
            <th className="w-[150px] px-4 py-3 text-center text-base font-medium text-gray-200">
              일시
            </th>
          </tr>
        </thead>
      </table>
      <ScrollArea className="h-[300px] w-full bg-gray-900">
        <ScrollAreaViewport>
          <table className="w-full table-fixed">
            <tbody className="bg-gray-900">
              {data.map((item) => (
                <tr key={item.timestamp} className="border-b border-gray-700">
                  <td className="w-[50px] px-4 py-3 text-center">
                    <span
                      className={`text-base font-medium ${
                        item.eventType === "연체" ? "text-fail" : "text-white"
                      }`}
                    >
                      {item.eventType}
                    </span>
                  </td>
                  <td className="w-[120px] px-4 py-3 text-center">
                    <div className="flex items-center justify-center gap-2 text-base font-medium">
                      {item.intAmt ? item.intAmt.toLocaleString() + "₩" : "-"}
                    </div>
                  </td>
                  <td className="w-[120px] px-4 py-3 text-center text-base text-white">
                    <div
                      className="hover:text-brand-primary mx-auto max-w-[180px] cursor-pointer truncate"
                      onClick={() => {
                        setSelectedWallet(item.from || "-");
                        setIsOpen(true);
                      }}
                    >
                      {item.from ? item.from : "-"}
                    </div>
                  </td>
                  <td className="w-[120px] px-4 py-3 text-center">
                    <div
                      className="hover:text-brand-primary mx-auto max-w-[180px] cursor-pointer truncate"
                      onClick={() => {
                        setSelectedWallet(item.to || "-");
                        setIsOpen(true);
                      }}
                    >
                      {item.to ? item.to : "-"}
                    </div>
                  </td>
                  <td className="w-[150px] px-4 py-3 text-center">
                    <div className="mx-auto w-fit text-base text-white">
                      {new Date(item.timestamp).toLocaleString()}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </ScrollAreaViewport>
        <ScrollAreaScrollbar
          orientation="vertical"
          className="w-[9px] bg-white py-1 opacity-100"
        ></ScrollAreaScrollbar>
      </ScrollArea>

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
    </div>
  );
};

export default NFTEventList;
