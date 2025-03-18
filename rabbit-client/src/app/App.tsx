import { Route, Routes } from "react-router";
import { lazy, Suspense } from "react";
import {
  LoanRoutes,
  AuctionRoutes,
  ContractRoutes,
  AccountRoutes,
} from "./routes";
import { NotFound } from "@/pages/common";
import Header from "@/widget/common/ui/Header";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import SubNav from "@/features/common/ui/SubNav";

const Home = lazy(() => import("@/pages/common/ui/Home"));

function App() {
  const isDesktop = useMediaQuery("md");
  return (
    <div className="mx-auto max-w-[1440px]">
      {isDesktop && <Header />}
      {isDesktop && <SubNav />}
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
    </div>
  );
}

export default App;
