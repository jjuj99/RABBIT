import { AccountHistoryType } from "@/entities/account/types/response";
import AccountHistoryCard from "@/entities/account/ui/AccountHistoryCard";
import { ScrollArea } from "@/shared/ui/scroll-area";
import { Separator } from "@/shared/ui/Separator";
interface AccountHistoryResponse {
  data: AccountHistoryType[];
}
const AccountHistory = ({ data }: AccountHistoryResponse) => {
  return (
    <div className="flex flex-col gap-6 rounded-md bg-gray-900 p-6">
      <h3 className="text-xl md:text-2xl">입출금 내역</h3>
      <div className="flex flex-col gap-3">
        <div className="border-gradient flex items-center justify-between rounded-md px-6 py-3">
          <span>종류</span>
          <span>금액</span>
        </div>
        <ScrollArea className="h-[calc(100vh-344px)]">
          <ul className="flex flex-col gap-3">
            {data.map((item) => (
              <>
                <AccountHistoryCard key={item.date} data={item} />
                <Separator />
              </>
            ))}
          </ul>
        </ScrollArea>
      </div>
    </div>
  );
};

export default AccountHistory;
