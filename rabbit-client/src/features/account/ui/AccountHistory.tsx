import { AccountHistoryType } from "@/entities/account/types/response";
import AccountHistoryCard from "@/entities/account/ui/AccountHistoryCard";
import { MenubarSeparator } from "@/shared/ui/menubar";
import { ScrollArea } from "@/shared/ui/scroll-area";
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
                <MenubarSeparator className="h-[0.2px] w-full bg-gray-300" />
              </>
            ))}
          </ul>
        </ScrollArea>
      </div>
    </div>
  );
};

export default AccountHistory;
