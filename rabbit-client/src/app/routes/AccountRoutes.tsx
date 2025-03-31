import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router";

const CoinCharge = lazy(() => import("@/pages/account/ui/CoinCharge"));
const CoinWithdraw = lazy(() => import("@/pages/account/ui/CoinWithdraw"));
const SuccessPage = lazy(() => import("@/entities/account/ui/toss/Success"));
const FailPage = lazy(() => import("@/entities/account/ui/toss/Fail"));
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
      <Route
        path="success"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <SuccessPage />
          </Suspense>
        }
      />
      <Route
        path="fail"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <FailPage />
          </Suspense>
        }
      />
    </Routes>
  );
};

export default AccountRoutes;
