import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/shared/ui/dropdown-menu";
import { ContractStatus, statusConfig } from "../constant/statusConfig";
import { Button } from "@/shared/ui/button";
import { Filter } from "lucide-react";
import { cn } from "@/shared/lib/utils";

export type SelectedFilters = Set<ContractStatus>;
// 필터 옵션 컴포넌트
interface FilterOptionsProps {
  selectedFilters: SelectedFilters;
  onFilterChange: (filters: SelectedFilters) => void;
}

const FilterOptions = ({
  selectedFilters,
  onFilterChange,
}: FilterOptionsProps) => {
  const handleFilterToggle = (status: ContractStatus) => {
    const newFilters = new Set(selectedFilters);
    if (newFilters.has(status)) {
      newFilters.delete(status);
    } else {
      newFilters.add(status);
    }
    onFilterChange(newFilters);
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className="border-gray-700 bg-gray-800/50 text-white hover:bg-gray-700/50"
        >
          <Filter className="mr-2 h-4 w-4" />
          상태 필터
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48 bg-gray-800 text-white">
        <DropdownMenuLabel>상태 선택</DropdownMenuLabel>
        <DropdownMenuSeparator className="bg-gray-700" />
        {(Object.keys(statusConfig) as ContractStatus[]).map((status) => (
          <DropdownMenuItem
            key={status}
            className={cn(
              "cursor-pointer focus:bg-gray-700",
              selectedFilters.has(status) && "bg-gray-700",
            )}
            onClick={() => handleFilterToggle(status)}
          >
            <div className="flex items-center gap-2">
              <span
                className={cn(
                  "h-2 w-2 rounded-full",
                  statusConfig[status].dotColor,
                )}
              />
              {statusConfig[status].label}
            </div>
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default FilterOptions;
