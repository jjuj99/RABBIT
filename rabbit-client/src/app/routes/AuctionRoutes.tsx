import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router";

const AuctionHistory = lazy(() => import("@/pages/auction/ui/AuctionHistory"));
const AuctionCreate = lazy(() => import("@/pages/auction/ui/AuctionCreate"));
const AuctionList = lazy(() => import("@/pages/auction/ui/AuctionList"));
const AuctionDetail = lazy(() => import("@/pages/auction/ui/AuctionDetail"));

const AuctionRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="list" replace />} />
      <Route
        path="history"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <AuctionHistory />
          </Suspense>
        }
      />
      <Route
        path="create"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <AuctionCreate />
          </Suspense>
        }
      />
      <Route
        path="list"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <AuctionList />
          </Suspense>
        }
      />
      <Route
        path=":auctionId"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <AuctionDetail />
          </Suspense>
        }
      />
    </Routes>
  );
};
export default AuctionRoutes;
