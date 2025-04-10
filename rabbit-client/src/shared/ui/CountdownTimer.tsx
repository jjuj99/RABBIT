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

interface CountdownTimerProps {
  endDate: string;
  status: auctionStatus;
  mineFlag: boolean;
  auctionId: string;
}

function CountdownTimer({
  endDate,
  status,
  mineFlag,
  auctionId,
}: CountdownTimerProps) {
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

  if (status === "ING") {
    if (mineFlag) {
      return (
        <>
          <button
            onClick={handleButtonClick}
            className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
          >
            {time}
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
}

export default CountdownTimer;
