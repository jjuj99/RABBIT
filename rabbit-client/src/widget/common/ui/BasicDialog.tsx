import { Button } from "@/shared/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/shared/ui/dialog";

interface BasicDialogProps {
  children: React.ReactNode;
  title: string;
  description: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onCancel?: () => void;
  onConfirm: () => void;
  cancelText?: string;
  confirmText?: string;
}

const BasicDialog = ({
  children,
  title,
  description,
  open,
  setOpen,
  onCancel,
  onConfirm,
  cancelText = "취소",
  confirmText = "확인",
}: BasicDialogProps) => {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <div className="flex justify-end gap-3">
          {onCancel && (
            <Button variant="secondary" onClick={onCancel}>
              {cancelText}
            </Button>
          )}
          <Button variant="primary" onClick={onConfirm}>
            {confirmText}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default BasicDialog;
