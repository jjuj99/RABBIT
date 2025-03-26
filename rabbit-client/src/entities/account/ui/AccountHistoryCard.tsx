import { cn } from "@/shared/lib/utils";
import currencyFormat from "@/shared/utils/currencyFormat";
import dateFormat from "@/shared/utils/dateFormat";
import timeFormat from "@/shared/utils/timeFormat";
import { AccountHistoryType } from "../types/response";

interface AccountHistoryCardProps {
  data: AccountHistoryType;
}

const AccountHistoryCard = ({ data }: AccountHistoryCardProps) => {
  return (
    <li className="flex items-center justify-between bg-gray-800 px-6 py-4">
      <div className="flex flex-col gap-2">
        <span
          className={cn(
            "text-base font-medium md:text-lg",
            data.type === "deposit" ? "text-fail" : "text-positive",
          )}
        >
          {data.type === "deposit" ? "입금" : "출금"}
        </span>
        <div className="flex flex-col gap-1 md:flex-row md:items-center">
          <span className="text-base font-medium md:text-lg">
            {dateFormat(data.date)}
          </span>
          <span className="text-xs font-light md:text-sm">
            {timeFormat(data.date)}
          </span>
        </div>
      </div>
      <div className="flex items-center gap-1">
        <span className="text-base font-bold md:text-lg">
          {currencyFormat(data.amount)}원
        </span>
      </div>
    </li>
  );
};

export default AccountHistoryCard;
