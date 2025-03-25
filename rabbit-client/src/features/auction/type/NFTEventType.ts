import { ColumnDef } from "@tanstack/react-table";

export type NFTEventType = {
  transactionType: string;
  transactionAmount: number;
  sender: string;
  receiver: string;
  transactionDate: string;
};

export const columns: ColumnDef<NFTEventType>[] = [
  {
    accessorKey: "transactionType",
    header: "Status",
  },
  {
    accessorKey: "transactionAmount",
    header: "Email",
  },
  {
    accessorKey: "sender",
    header: "Amount",
  },
  {
    accessorKey: "receiver",
    header: "Amount",
  },
  {
    accessorKey: "transactionDate",
    header: "Amount",
  },
];
