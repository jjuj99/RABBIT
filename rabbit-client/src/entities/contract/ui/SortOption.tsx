import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/shared/ui/dropdown-menu";
import { ArrowDownAZ, ArrowUpAZ } from "lucide-react";

// 정렬/필터 타입 정의 수정
type SortField = "createdAt" | "amount";
type SortOrder = "asc" | "desc";

export interface SortConfig {
  field: SortField;
  order: SortOrder;
}

interface SortOptionsProps {
  currentSort: SortConfig;
  onSortChange: (config: SortConfig) => void;
}

// 정렬 옵션 컴포넌트 수정
const SortOptions = ({ onSortChange, currentSort }: SortOptionsProps) => {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className="border-gray-700 bg-gray-800/50 text-white hover:bg-gray-700/50"
        >
          {currentSort.field === "createdAt" ? "요청일" : "대출 금액"}
          {currentSort.order === "asc" ? (
            <ArrowUpAZ className="ml-2 h-4 w-4" />
          ) : (
            <ArrowDownAZ className="ml-2 h-4 w-4" />
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48 bg-gray-800 text-white">
        <DropdownMenuLabel>정렬 기준</DropdownMenuLabel>
        <DropdownMenuSeparator className="bg-gray-700" />
        <DropdownMenuItem
          className={cn(
            "cursor-pointer focus:bg-gray-700",
            currentSort.field === "createdAt" && "bg-gray-700",
          )}
          onClick={() =>
            onSortChange({
              field: "createdAt",
              order:
                currentSort.field === "createdAt" && currentSort.order === "asc"
                  ? "desc"
                  : "asc",
            })
          }
        >
          <div className="flex w-full items-center justify-between">
            <span>요청일</span>
            {currentSort.field === "createdAt" &&
              {
                asc: <ArrowUpAZ className="h-4 w-4" />,
                desc: <ArrowDownAZ className="h-4 w-4" />,
              }[currentSort.order]}
          </div>
        </DropdownMenuItem>
        <DropdownMenuItem
          className={cn(
            "cursor-pointer focus:bg-gray-700",
            currentSort.field === "amount" && "bg-gray-700",
          )}
          onClick={() =>
            onSortChange({
              field: "amount",
              order:
                currentSort.field === "amount" && currentSort.order === "asc"
                  ? "desc"
                  : "asc",
            })
          }
        >
          <div className="flex w-full items-center justify-between">
            <span>대출 금액</span>
            {currentSort.field === "amount" &&
              {
                asc: <ArrowUpAZ className="h-4 w-4" />,
                desc: <ArrowDownAZ className="h-4 w-4" />,
              }[currentSort.order]}
          </div>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default SortOptions;
