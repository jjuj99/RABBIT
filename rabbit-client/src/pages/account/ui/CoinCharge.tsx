import { AccountNav, CoinStatus } from "@/entities/account";
import { AccountHistoryType } from "@/entities/account/types/response";
import { CoinChargeForm } from "@/features/account";
import AccountHistory from "@/features/account/ui/AccountHistory";
import { useState } from "react";

const CoinCharge = () => {
  const amountState = useState(0);
  const mock: AccountHistoryType[] = [
    {
      type: "deposit",
      amount: 10000,
      date: "2025-03-25",
    },
    {
      type: "withdraw",
      amount: 10000,
      date: "2025-03-25",
    },
  ];

  return (
    <main className="grid grid-cols-1 gap-6 md:grid-cols-2">
      <div className="flex flex-col gap-6">
        <CoinStatus />
        {/* 충전 출금 네비게이션 */}
        <AccountNav />

        {/* 충전폼 */}
        <CoinChargeForm amountState={amountState} />
      </div>

      <AccountHistory data={mock} />
    </main>
  );
};

export default CoinCharge;
