import { withLazyComponent } from "@/widget/common/lib/withLazyComponent";
import { Navigate, Route, Routes } from "react-router";

const BorrowList = withLazyComponent(
  () => import("@/pages/loan/ui/BorrowList"),
);
const LentList = withLazyComponent(() => import("@/pages/loan/ui/LentList"));
const BorrowDetail = withLazyComponent(
  () => import("@/pages/loan/ui/BorrowDetail"),
);
const LentDetail = withLazyComponent(
  () => import("@/pages/loan/ui/LentDetail"),
);

const LoanRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="borrow" replace />} />
      <Route path="borrow" element={<BorrowList />} />
      <Route path="lent" element={<LentList />} />
      <Route path="borrow/:contractId" element={<BorrowDetail />} />
      <Route path="lent/:contractId" element={<LentDetail />} />
    </Routes>
  );
};
export default LoanRoutes;
