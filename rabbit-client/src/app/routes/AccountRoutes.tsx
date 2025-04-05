import { withLazyComponent } from "@/widget/common/lib/withLazyComponent";
import { Navigate, Route, Routes } from "react-router";

const CoinCharge = withLazyComponent(
  () => import("@/pages/account/ui/CoinCharge"),
);
const CoinWithdraw = withLazyComponent(
  () => import("@/pages/account/ui/CoinWithdraw"),
);
const SuccessPage = withLazyComponent(
  () => import("@/entities/account/ui/toss/Success"),
);
const FailPage = withLazyComponent(
  () => import("@/entities/account/ui/toss/Fail"),
);
const AccountRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="charge" replace />} />
      <Route path="charge" element={<CoinCharge />} />
      <Route path="withdraw" element={<CoinWithdraw />} />
      <Route path="success" element={<SuccessPage />} />
      <Route path="fail" element={<FailPage />} />
    </Routes>
  );
};

export default AccountRoutes;
