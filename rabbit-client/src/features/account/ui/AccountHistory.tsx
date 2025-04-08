import AccountHistoryCard from "@/entities/account/ui/AccountHistoryCard";
import { AccountHistoryResponse } from "@/entities/coin/api/coinApi";
import { ScrollArea } from "@/shared/ui/scroll-area";
import { Separator } from "@/shared/ui/Separator";
import React from "react";

const AccountHistory = ({
  data,
}: {
  data: AccountHistoryResponse[] | undefined;
}) => {
  return (
    <div className="flex flex-col gap-6 rounded-md bg-gray-900 p-6">
      <h3 className="text-xl md:text-2xl">입출금 내역</h3>
      <div className="flex flex-col gap-3">
        <div className="border-gradient flex items-center justify-between rounded-md px-6 py-3">
          <span>종류</span>
          <span>금액</span>
        </div>
        {!data ? (
          <span>입출금 내역이 없습니다.</span>
        ) : (
          <ScrollArea className="h-[calc(100vh-344px)]">
            <ul className="flex flex-col gap-3">
              {data.map((item, index) => (
                <React.Fragment key={item.createdAt + item.type + index}>
                  <AccountHistoryCard key={item.createdAt} data={item} />
                  <Separator />
                </React.Fragment>
              ))}
            </ul>
          </ScrollArea>
        )}
      </div>
    </div>
  );
};

export default AccountHistory;
