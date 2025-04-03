import { BidHistoryResponse } from "@/features/auction/types/response";
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDateToYMD(dateStr: string) {
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}년 ${month}월 ${day}일`;
}

export const getBidStatusColor = (status: BidHistoryResponse["bidStatus"]) => {
  switch (status) {
    case "WON":
      return "text-brand-primary";
    case "LOST":
      return "text-fail";
    case "PENDING":
      return "text-positive";
    default:
      return "text-gray-600";
  }
};
