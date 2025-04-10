import { withLazyComponent } from "@/widget/common/lib/withLazyComponent";
import { Navigate, Route, Routes } from "react-router";

const AuctionHistory = withLazyComponent(
  () => import("@/pages/auction/ui/AuctionHistory"),
);
const AuctionCreate = withLazyComponent(
  () => import("@/pages/auction/ui/AuctionCreate"),
);
const AuctionList = withLazyComponent(
  () => import("@/pages/auction/ui/AuctionList"),
);
const AuctionDetail = withLazyComponent(
  () => import("@/pages/auction/ui/AuctionDetail"),
);

const AuctionRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="list" replace />} />
      <Route path="list" element={<AuctionList />} />
      <Route path="history" element={<AuctionHistory />} />
      <Route path="new" element={<AuctionCreate />} />
      <Route path=":auctionId" element={<AuctionDetail />} />
    </Routes>
  );
};
export default AuctionRoutes;
