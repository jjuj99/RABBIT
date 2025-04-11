import { useNavigate } from "react-router";
import useCountdown from "../hooks/useCountdown";
import { auctionStatus } from "../type/Types";
import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/shared/ui/dialog";
import { Button } from "@/shared/ui/button";
import { AuctionForceEndAPI } from "@/features/auction/api/auctionApi";
import LoadingOverlay from "@/widget/common/ui/LoadingOverray";

interface AuctionCountdownTimerProps {
  endDate: string;
  status: auctionStatus;
  mineFlag: boolean;
  auctionId: string;
}

const AuctionCountdownTimer = ({
  endDate,
  status,
  mineFlag,
  auctionId,
}: AuctionCountdownTimerProps) => {
  const time = useCountdown(endDate);
  const navigate = useNavigate();
  const [showDialog, setShowDialog] = useState(false);

  const forceEndMutation = useMutation({
    mutationFn: () => AuctionForceEndAPI({ auctionId: parseInt(auctionId) }),
    onSuccess: () => {
      setShowDialog(true);
    },
    onError: (error) => {
      console.error(error);
      toast.error("경매 강제 종료에 실패했습니다.");
    },
  });

  const handleButtonClick = () => {
    forceEndMutation.mutate();
  };

  const handleDialogConfirm = () => {
    setShowDialog(false);
    navigate("/auction/list");
  };

  if (status === "ING" && new Date(endDate) < new Date()) {
    if (mineFlag) {
      return (
        <>
          <LoadingOverlay
            isLoading={forceEndMutation.isPending}
            content={[
              "경매 종료 중...",
              "최대 2분 소요됩니다...",
              "Seporia 네트워크에 연결중...",
              "메타마스크 확인 중...",
              "부속 NFT 발행 중",
              "소유권 이전 중",
            ]}
          />
          <button
            onClick={handleButtonClick}
            className="bg-brand-primary h-fit w-fit rounded px-4 py-2 text-lg whitespace-nowrap text-black hover:bg-green-500"
          >
            경매 종료
          </button>
          <Dialog open={showDialog} onOpenChange={setShowDialog}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>경매 강제 종료</DialogTitle>
                <DialogDescription>
                  경매가 성공적으로 종료되었습니다. 목록 페이지로 이동합니다.
                </DialogDescription>
              </DialogHeader>
              <DialogFooter>
                <Button onClick={handleDialogConfirm}>확인</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </>
      );
    }
    return <span>경매 종료됨</span>;
  } else if (status === "ING") {
    return <span>{time}</span>;
  }

  if (status === "COMPLETED") {
    return <span>경매 종료됨</span>;
  }

  if (status === "FAILED") {
    return <span>유찰됨</span>;
  }

  if (status === "CANCELED") {
    return <span>취소됨</span>;
  }

  return <span>{time}</span>;
};

export default AuctionCountdownTimer;
