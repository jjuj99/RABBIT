export type ContractStatus =
  | "REQUESTED"
  | "MODIFICATION_REQUESTED"
  | "CONTRACTED"
  | "CANCELED"
  | "REJECTED";

export const statusConfig: Record<
  ContractStatus,
  { className: string; dotColor: string }
> = {
  REQUESTED: {
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-blue-400",
  },
  MODIFICATION_REQUESTED: {
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-amber-400",
  },
  CONTRACTED: {
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-emerald-400",
  },
  CANCELED: {
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-rose-400",
  },
  REJECTED: {
    className:
      "bg-gradient-to-r from-gray-800 to-gray-700 text-white hover:from-gray-700 hover:to-gray-600",
    dotColor: "bg-red-400",
  },
};
