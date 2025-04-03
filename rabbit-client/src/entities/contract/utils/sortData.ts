import { ContractListResponse } from "../types/response";
import { SortConfig } from "../ui/SortOption";

// 정렬 함수
export const sortData = (
  data: ContractListResponse[],
  sortConfig: SortConfig,
) => {
  return [...data].sort((a, b) => {
    if (sortConfig.field === "createdAt") {
      const dateA = new Date(a.createdAt.replace(/\./g, "-"));
      const dateB = new Date(b.createdAt.replace(/\./g, "-"));
      return sortConfig.order === "asc"
        ? dateA.getTime() - dateB.getTime()
        : dateB.getTime() - dateA.getTime();
    }

    if (sortConfig.field === "amount") {
      return sortConfig.order === "asc"
        ? a.amount - b.amount
        : b.amount - a.amount;
    }

    return 0;
  });
};
