import { Button } from "@/shared/ui/button";
import {
  DialogHeader,
  Dialog,
  DialogContent,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/shared/ui/dialog";
import { Textarea } from "@/shared/ui/textarea";

import { useState } from "react";

interface RejectDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onReject: (message: string) => void;
  onModify: (message: string) => void;
}

const RejectDialog = ({
  isOpen,
  onOpenChange,
  onReject,
  onModify,
}: RejectDialogProps) => {
  const [rejectMessage, setRejectMessage] = useState("");

  const handleRejectConfirm = () => {
    onReject(rejectMessage);
    setRejectMessage("");
  };

  const handleModificationRequest = () => {
    onModify(rejectMessage);
    setRejectMessage("");
  };

  const handleClose = () => {
    setRejectMessage("");
    onOpenChange(false);
  };

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>차용증 요청 처리</DialogTitle>
          <DialogDescription>
            거절 또는 수정 요청 사유를 입력해주세요.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <Textarea
            placeholder="메시지를 입력해주세요..."
            value={rejectMessage}
            onChange={(e) => setRejectMessage(e.target.value)}
            className="h-32"
          />
        </div>
        <DialogFooter className="flex gap-2">
          <Button
            variant="destructive"
            onClick={handleRejectConfirm}
            disabled={!rejectMessage.trim()}
          >
            거절하기
          </Button>
          <Button
            variant="secondary"
            onClick={handleModificationRequest}
            disabled={!rejectMessage.trim()}
          >
            수정 요청
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default RejectDialog;
