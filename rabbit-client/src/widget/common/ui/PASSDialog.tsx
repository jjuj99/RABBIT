import { ReactNode, useState } from "react";
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
import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { passType } from "@/shared/type/Types";

interface PASSDialogProps {
  children: ReactNode;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  setPassState: (state: passType) => void;
}

const PASSDialog = ({
  children,
  isOpen,
  onOpenChange,
  setPassState,
}: PASSDialogProps) => {
  const { user } = useAuthUser();
  const [userName, setUserName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const handleComplete = (phoneNumber: string, name: string) => {
    const isVerified = name === user?.userName;
    if (isVerified) {
      const passResult: passType = {
        phoneNumber: phoneNumber,
        name: name,
        passAuthToken: isVerified
          ? btoa(encodeURIComponent(name + phoneNumber))
          : "",
        txId: "rabbit",
        authResultCode: isVerified ? "SUCCESS" : "FAIL",
      };
      setPassState(passResult);
      return true;
    }
    return false;
  };

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
          onUserNameChange={setUserName}
          onPhoneNumberChange={setPhoneNumber}
          onComplete={handleComplete}
          onClose={() => onOpenChange(false)}
        />
      </DialogContent>
    </Dialog>
  );
};

export default PASSDialog;
