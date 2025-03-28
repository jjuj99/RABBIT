import { DataTable } from "@/shared/ui/DataTable";
import { columns } from "../../../entities/auction/types/NFTEventType";

const AuctionNFTEventList = () => {
  const data = [
    {
      transactionType: "입금",
      transactionAmount: "500000",
      sender: "홍길동",
      receiver: "김철수",
      transactionDate: "2025-03-25 14:32",
    },
    {
      transactionType: "출금",
      transactionAmount: "20,000,000,000",
      sender: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      receiver: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      transactionDate: "2025-03-24 09:15",
    },
    {
      transactionType: "이체",
      transactionAmount: "100000",
      sender: "이영희",
      receiver: "박민수",
      transactionDate: "2025-03-23 18:47",
    },
    {
      transactionType: "입금",
      transactionAmount: "500000",
      sender: "홍길동",
      receiver: "김철수",
      transactionDate: "2025-03-25 14:32",
    },
    {
      transactionType: "출금",
      transactionAmount: "20,000,000,000",
      sender: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      receiver: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      transactionDate: "2025-03-24 09:15",
    },
    {
      transactionType: "이체",
      transactionAmount: "100000",
      sender: "이영희",
      receiver: "박민수",
      transactionDate: "2025-03-23 18:47",
    },
    {
      transactionType: "입금",
      transactionAmount: "500000",
      sender: "홍길동",
      receiver: "김철수",
      transactionDate: "2025-03-25 14:32",
    },
    {
      transactionType: "출금",
      transactionAmount: "20,000,000,000",
      sender: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      receiver: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      transactionDate: "2025-03-24 09:15",
    },
    {
      transactionType: "이체",
      transactionAmount: "100000",
      sender: "이영희",
      receiver: "박민수",
      transactionDate: "2025-03-23 18:47",
    },
    {
      transactionType: "입금",
      transactionAmount: "500000",
      sender: "홍길동",
      receiver: "김철수",
      transactionDate: "2025-03-25 14:32",
    },
    {
      transactionType: "출금",
      transactionAmount: "20,000,000,000",
      sender: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      receiver: "0x742d35Cc6634C0532925a3b844Bc454e4438f44e",
      transactionDate: "2025-03-24 09:15",
    },
    {
      transactionType: "이체",
      transactionAmount: "100000",
      sender: "이영희",
      receiver: "박민수",
      transactionDate: "2025-03-23 18:47",
    },
  ];

  return (
    <div>
      <DataTable columns={columns} data={data} />
    </div>
  );
};

export default AuctionNFTEventList;
