import { cn } from "@/shared/lib/utils";
import { Badge } from "@/shared/ui/badge";
import { ContractStatus, statusConfig } from "../constant/statusConfig";

interface StatusBadgeProps {
  status: ContractStatus;
}

const ContactStatusBadge = ({ status }: StatusBadgeProps) => {
  return (
    <Badge
      className={cn(
        "flex items-center gap-1.5 rounded-full border border-gray-600/50 px-3 py-1 text-xs font-medium tracking-wide shadow-md",
        statusConfig[status].className,
      )}
    >
      <span
        className={cn(
          "inline-block h-2 w-2 rounded-full shadow-sm",
          statusConfig[status].dotColor,
        )}
      />
      {statusConfig[status].label}
    </Badge>
  );
};

export default ContactStatusBadge;
