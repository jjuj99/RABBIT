import { lazy, Suspense } from "react";
import { Navigate, Route, Routes, Outlet } from "react-router";

const ContractCreate = lazy(() => import("@/pages/contract/ui/ContractCreate"));
const ContractDetail = lazy(() => import("@/pages/contract/ui/ContractDetail"));
const ContractRequestSuccess = lazy(
  () => import("@/pages/contract/ui/ContractRequestSuccess"),
);
const ContractReceivedList = lazy(
  () => import("@/pages/contract/ui/ContractReceivedList"),
);
const ContractSentList = lazy(
  () => import("@/pages/contract/ui/ContractSentList"),
);
const ContractFinalReview = lazy(
  () => import("@/pages/contract/ui/ContractFinalReview"),
);

const ContractLayout = () => {
  return (
    <div>
      {/* 공통 헤더나 레이아웃 요소들 */}
      <Suspense fallback={<div>로딩중...</div>}>
        <Outlet /> {/* 여기에 자식 라우트의 컴포넌트가 렌더링됨 */}
      </Suspense>
    </div>
  );
};

const ContractRoutes = () => {
  return (
    <Routes>
      <Route element={<ContractLayout />}>
        <Route path="/" element={<Navigate to="new" replace />} />
        <Route path="new" element={<ContractCreate />} />
        <Route
          path="/new/request-success"
          element={<ContractRequestSuccess />}
        />
        <Route path="sent" element={<ContractSentList />} />
        <Route path="received" element={<ContractReceivedList />} />
        <Route
          path="/received/final-review"
          element={<ContractFinalReview />}
        />
        <Route path=":contractId" element={<ContractDetail />} />
      </Route>
    </Routes>
  );
};

export default ContractRoutes;
