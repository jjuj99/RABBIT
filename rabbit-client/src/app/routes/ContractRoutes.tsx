import { withLazyComponent } from "@/widget/common/lib/withLazyComponent";
import { Navigate, Route, Routes } from "react-router";

const ContractCreate = withLazyComponent(
  () => import("@/pages/contract/ui/ContractCreate"),
);
const ContractDetail = withLazyComponent(
  () => import("@/pages/contract/ui/ContractDetail"),
);
const ContractRequestSuccess = withLazyComponent(
  () => import("@/pages/contract/ui/ContractRequestSuccess"),
);
const ContractReceivedList = withLazyComponent(
  () => import("@/pages/contract/ui/ContractReceivedList"),
);
const ContractSentList = withLazyComponent(
  () => import("@/pages/contract/ui/ContractSentList"),
);
const ContractFinalReview = withLazyComponent(
  () => import("@/pages/contract/ui/ContractFinalReview"),
);

const ContractRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="new" replace />} />
      <Route path="new" element={<ContractCreate />} />
      <Route path="/new/request-success" element={<ContractRequestSuccess />} />
      <Route path="sent" element={<ContractSentList />} />
      <Route path="received" element={<ContractReceivedList />} />
      <Route path="/received/final-review" element={<ContractFinalReview />} />
      <Route path="sent/:contractId" element={<ContractDetail />} />
      <Route path="received/:contractId" element={<ContractDetail />} />
    </Routes>
  );
};

export default ContractRoutes;
