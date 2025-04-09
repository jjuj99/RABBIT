import { Copy, Check } from "lucide-react";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle } from "@/shared/ui/dialog";
import { NFTEvent } from "@/shared/type/NFTEventList";
import { Separator } from "@/shared/ui/Separator";
import { ScrollArea } from "@/shared/ui/scroll-area";
import {
  ScrollAreaScrollbar,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";

interface NFTEventListMobileProps {
  data?: NFTEvent[];
}

const NFTEventListMobile = ({ data }: NFTEventListMobileProps) => {
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
      <div className="flex h-[100px] items-center justify-center rounded-sm bg-gray-900">
        <div className="text-base text-gray-200">이벤트 정보가 없습니다.</div>
      </div>
    );
  }

  return (
    <>
      <ScrollArea className="h-[600px] w-full">
        <ScrollAreaViewport>
          <div className="flex flex-col gap-4 px-2 py-2">
            {data.map((item) => (
              <div key={item.timestamp}>
                <div className="rounded-sm bg-gray-800 px-4 py-3">
                  <div className="flex flex-col gap-3">
                    <div>
                      <div className="mb-2 flex items-center justify-between">
                        <span
                          className={`text-base font-medium ${item.eventType === "연체" ? "text-fail" : "text-white"}`}
                        >
                          {item.eventType}
                        </span>
                        <span className="text-sm text-gray-200">
                          {new Date(item.timestamp).toLocaleString()}
                        </span>
                      </div>
                      <Separator />
                    </div>
                    <div className="flex flex-col gap-2">
                      <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-100">금액</span>
                        <span className="text-sm text-white">
                          {item.intAmt
                            ? item.intAmt.toLocaleString() + " ₩"
                            : "-"}
                        </span>
                      </div>

                      <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-100">전송인</span>
                        <div
                          className="hover:text-brand-primary max-w-[180px] cursor-pointer truncate text-sm text-white"
                          onClick={() => {
                            setSelectedWallet(item.from || "-");
                            setIsOpen(true);
                          }}
                        >
                          {item.from ? item.from : "-"}
                        </div>
                      </div>

                      <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-100">수신인</span>
                        <div
                          className="hover:text-brand-primary max-w-[180px] cursor-pointer truncate text-sm text-white"
                          onClick={() => {
                            setSelectedWallet(item.to || "-");
                            setIsOpen(true);
                          }}
                        >
                          {item.to ? item.to : "-"}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </ScrollAreaViewport>
        <ScrollAreaScrollbar
          orientation="vertical"
          className="w-[9px] bg-gray-900 py-1 opacity-100"
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
    </>
  );
};

export default NFTEventListMobile;
