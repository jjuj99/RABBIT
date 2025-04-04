import SubNav from "@/features/common/ui/SubNav";
import { NotFound } from "@/pages/common";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import Header from "@/widget/common/ui/Header";
import { lazy, Suspense } from "react";
import { Route, Routes } from "react-router";
import {
  AccountRoutes,
  AuctionRoutes,
  ContractRoutes,
  LoanRoutes,
} from "./routes";
import HeaderMobile from "@/widget/common/ui/HeaderMobile";
import MainNavMobile from "@/features/common/ui/MainNavMobile";

const Home = lazy(() => import("@/pages/common/ui/Home"));

function App() {
  const isDesktop = useMediaQuery("md");

  return (
    <div className="mx-auto max-w-[1440px] px-4 pb-20 sm:px-8">
      {isDesktop ? <Header /> : <HeaderMobile />}
      {isDesktop ? <SubNav /> : <SubNav className="pb-0" />}
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
      {!isDesktop && (
        <div className="fixed right-0 bottom-0 left-0">
          <MainNavMobile />
        </div>
      )}
    </div>
  );
}

export default App;
