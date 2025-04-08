import { ColumnDef } from "@tanstack/react-table";

export type NFTEventType = {
  transactionType: string;
  transactionAmount?: string;
  sender: string;
  receiver: string;
  transactionDate: string;
};

export const columns: ColumnDef<NFTEventType>[] = [
  {
    accessorKey: "transactionType",
    header: "종류",
  },
  {
    accessorKey: "transactionAmount",
    header: "금액",
  },
  {
    accessorKey: "sender",
    header: "전송인",
  },
  {
    accessorKey: "receiver",
    header: "수신인",
  },
  {
    accessorKey: "transactionDate",
    header: "일시",
  },
];

export interface BidUpdateEvent {
  id: string;
  event: string;
  data: {
    bidId: number;
    bidAmount: number;
    createdAt: string;
  };
}
