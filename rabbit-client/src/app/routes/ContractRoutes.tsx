import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router";

const ContractCreate = lazy(() => import("@/pages/contract/ui/ContractCreate"));
const ContractDetail = lazy(() => import("@/pages/contract/ui/ContractDetail"));
const ContractReceivedList = lazy(
  () => import("@/pages/contract/ui/ContractReceivedList"),
);
const ContractSentList = lazy(
  () => import("@/pages/contract/ui/ContractSentList"),
);

const ContractRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="new" replace />} />
      <Route
        path="new"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <ContractCreate />
          </Suspense>
        }
      />
      <Route
        path="sent"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <ContractSentList />
          </Suspense>
        }
      />
      <Route
        path="received"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <ContractReceivedList />
          </Suspense>
        }
      />
      <Route
        path=":contractId"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <ContractDetail />
          </Suspense>
        }
      />
    </Routes>
  );
};

export default ContractRoutes;
