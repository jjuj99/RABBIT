import { ColumnDef } from "@tanstack/react-table";

export type NFTEventType = {
  transactionType: string;
  transactionAmount?: string;
  sender: string;
  receiver: string;
  transactionDate: string;
};
//타입 만들고 있습니다. column에 들어갈 데이터를 정의하고있어요.
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
