import { ReactNode } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogOverlay,
} from "@/shared/ui/dialog";
import PASS from "@/features/common/ui/PASS";

interface PASSDialogProps {
  children: ReactNode;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  userName: string;
  phoneNumber: string;
  onUserNameChange: (value: string) => void;
  onPhoneNumberChange: (value: string) => void;
  onComplete: (phoneNumber: string, name: string) => boolean;
}

const PASSDialog = ({
  children,
  isOpen,
  onOpenChange,
  userName,
  phoneNumber,
  onUserNameChange,
  onPhoneNumberChange,
  onComplete,
}: PASSDialogProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogOverlay className="bg-transparent" />
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="border-none bg-transparent p-0 shadow-none">
        <DialogHeader className="sr-only">
          <DialogTitle>PASS 인증</DialogTitle>
          <DialogDescription>
            PASS 인증을 통한 휴대폰 본인인증 진행
          </DialogDescription>
        </DialogHeader>
        <PASS
          userName={userName}
          phoneNumber={phoneNumber}
          onUserNameChange={onUserNameChange}
          onPhoneNumberChange={onPhoneNumberChange}
          onComplete={onComplete}
        />
      </DialogContent>
    </Dialog>
  );
};

export default PASSDialog;
