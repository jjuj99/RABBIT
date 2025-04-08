import { AccountNav, CoinStatus } from "@/entities/account";

import useGetTrasitionHistory from "@/entities/coin/hook/useGetTrasitionHistory";
import { CoinChargeForm } from "@/features/account";
import AccountHistory from "@/features/account/ui/AccountHistory";
import { useState } from "react";

const CoinCharge = () => {
  const amountState = useState(0);
  const { data, isLoading } = useGetTrasitionHistory();

  return (
    <main className="grid grid-cols-1 gap-6 md:grid-cols-2">
      <div className="flex flex-col gap-6">
        <CoinStatus />
        {/* 충전 출금 네비게이션 */}
        <AccountNav />

        {/* 충전폼 */}
        <CoinChargeForm amountState={amountState} />
      </div>

      <AccountHistory data={data?.data} />
    </main>
  );
};

export default CoinCharge;
