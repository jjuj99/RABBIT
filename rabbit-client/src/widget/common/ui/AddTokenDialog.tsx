import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import BasicDialog from "./BasicDialog";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { handleAddToken } from "@/entities/wallet/utils";

const AddTokenDialog = () => {
  const { walletAddress } = useAuthUser();
  const [open, setOpen] = useState(false);

  const shouldShowDialog = () => {
    const nextShowTime = localStorage.getItem("nextTokenDialogShowTime");
    if (!nextShowTime) return true;

    const currentTime = new Date().getTime();
    return currentTime >= parseInt(nextShowTime);
  };

  const handleCancel = () => {
    const nextShowTime = new Date().getTime() + 24 * 60 * 60 * 1000;
    localStorage.setItem("nextTokenDialogShowTime", nextShowTime.toString());
    setOpen(false);
    toast.info("하루동안 코인등록 팝업을 보지 않습니다.");
  };

  useEffect(() => {
    if (walletAddress && shouldShowDialog()) {
      setOpen(true);
    }
  }, [walletAddress]);

  return (
    <BasicDialog
      title="Rabbit 코인 등록하기"
      description="Rabbit 코인을 등록하려면 토큰을 추가해야 합니다."
      open={open}
      setOpen={setOpen}
      onCancel={handleCancel}
      onConfirm={handleAddToken}
      cancelText="나중에하기"
      confirmText="토큰 추가하기"
    >
      <div />
    </BasicDialog>
  );
};

export default AddTokenDialog;
