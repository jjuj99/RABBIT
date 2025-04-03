export type ContractStatus =
  | "REQUESTED"
  | "MODIFIED"
  | "COMPLETED"
  | "CANCELED";
export const statusConfig: Record<
  ContractStatus,
  { label: string; className: string; dotColor: string }
> = {
  REQUESTED: {
    label: "요청됨",
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-blue-400",
  },
  MODIFIED: {
    label: "수정 요청됨",
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-amber-400",
  },
  COMPLETED: {
    label: "체결됨",
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-emerald-400",
  },
  CANCELED: {
    label: "취소됨",
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-rose-400",
  },
};
