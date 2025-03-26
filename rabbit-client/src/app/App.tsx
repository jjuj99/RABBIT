import { Route, Routes } from "react-router";
import { lazy, Suspense } from "react";
import {
  LoanRoutes,
  AuctionRoutes,
  ContractRoutes,
  AccountRoutes,
} from "./routes";
import { NotFound } from "@/pages/common";

const Home = lazy(() => import("@/pages/common/ui/Home"));

function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <Suspense fallback={<div>로딩중...</div>}>
            <Home />
          </Suspense>
        }
      />
      <Route path="*" element={<NotFound />} />
      <Route path="/loan/*" element={<LoanRoutes />} />
      <Route path="/auction/*" element={<AuctionRoutes />} />
      <Route path="/contract/*" element={<ContractRoutes />} />
      <Route path="/account/*" element={<AccountRoutes />} />
    </Routes>
  );
}

export default App;
