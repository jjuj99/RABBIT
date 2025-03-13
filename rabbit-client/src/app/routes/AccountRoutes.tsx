import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router";

const CoinCharge = lazy(() => import("@/pages/account/ui/CoinCharge"));
const CoinWithdraw = lazy(() => import("@/pages/account/ui/CoinWithdraw"));

const AccountRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="charge" replace />} />
      <Route
        path="charge"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <CoinCharge />
          </Suspense>
        }
      />
      <Route
        path="withdraw"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <CoinWithdraw />
          </Suspense>
        }
      />
    </Routes>
  );
};

export default AccountRoutes;
