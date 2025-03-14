import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router";

const BorrowList = lazy(() => import("@/pages/loan/ui/BorrowList"));
const LentList = lazy(() => import("@/pages/loan/ui/LentList"));
const BorrowDetail = lazy(() => import("@/pages/loan/ui/BorrowDetail"));
const LentDetail = lazy(() => import("@/pages/loan/ui/LentDetail"));

const LoanRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="borrow" replace />} />
      <Route
        path="borrow"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <BorrowList />
          </Suspense>
        }
      />
      <Route
        path="lent"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <LentList />
          </Suspense>
        }
      />
      <Route
        path="borrow/:contractId"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <BorrowDetail />
          </Suspense>
        }
      />
      <Route
        path="lent/:contractId"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <LentDetail />
          </Suspense>
        }
      />
    </Routes>
  );
};
export default LoanRoutes;
