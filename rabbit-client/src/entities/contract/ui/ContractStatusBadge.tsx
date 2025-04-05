import { X } from "lucide-react";
import { cn } from "@/shared/lib/utils";
import { Badge } from "@/shared/ui/badge";
import { ContractStatus, statusConfig } from "../constant/statusConfig";
import useGetCommonCode from "@/widget/common/hook/useGetCommonCode";

interface StatusBadgeProps {
  status: ContractStatus;
  size?: "default" | "lg";
  onClick?: () => void; // 배지 클릭 핸들러
  onDelete?: () => void; // X 버튼 클릭 핸들러
}

const ContractStatusBadge = ({
  status,
  size = "default",
  onClick,
  onDelete,
}: StatusBadgeProps) => {
  const { data: commonCodeList } = useGetCommonCode("CONTRACT_STATUS");
  const statusName = commonCodeList?.data?.find(
    (code) => code.code === status,
  )?.codeName;

  return (
    <Badge
      className={cn(
        "flex items-center gap-1.5 rounded-full border border-gray-600/50 font-medium tracking-wide shadow-md",
        size === "default" && "px-3 py-1 text-xs",
        size === "lg" && "px-4 py-1.5 text-sm",
        onClick && "cursor-pointer hover:bg-gray-700/50",
        onDelete && "bg-gray-800 text-white", // 삭제 버튼이 있을 때의 스타일
        !onDelete && statusConfig[status].className, // 일반 상태 배지 스타일
      )}
      onClick={onClick}
    >
      <span
        className={cn(
          "inline-block rounded-full shadow-sm",
          size === "default" && "h-2 w-2",
          size === "lg" && "h-2.5 w-2.5",
          statusConfig[status].dotColor,
        )}
      />
      {statusName}
      {onDelete && (
        <button
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
          className="ml-1 hover:text-gray-400"
        >
          <X className="h-4 w-4" />
        </button>
      )}
    </Badge>
  );
};

export default ContractStatusBadge;
