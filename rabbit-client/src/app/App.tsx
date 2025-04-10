import SubNav from "@/features/common/ui/SubNav";
import { Home, NotFound } from "@/pages/common";
import useMediaQuery from "@/shared/hooks/useMediaQuery";
import Header from "@/widget/common/ui/Header";
import { Route, Routes } from "react-router";
import {
  AccountRoutes,
  AuctionRoutes,
  ContractRoutes,
  LoanRoutes,
} from "./routes";
import HeaderMobile from "@/widget/common/ui/HeaderMobile";
import MainNavMobile from "@/features/common/ui/MainNavMobile";
import Footer from "@/widget/common/ui/Footer";
import ProtectRoute from "@/widget/common/ui/ProtectRoute";

function App() {
  const isDesktop = useMediaQuery("md");

  return (
    <div className="mx-auto max-w-[1440px] px-4 pb-20 sm:px-8">
      {isDesktop ? <Header /> : <HeaderMobile />}
      {isDesktop ? <SubNav /> : <SubNav className="pb-0" />}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="*" element={<NotFound />} />
        <Route
          path="/loan/*"
          element={
            <ProtectRoute>
              <LoanRoutes />
            </ProtectRoute>
          }
        />
        <Route path="/auction/*" element={<AuctionRoutes />} />
        <Route
          path="/contract/*"
          element={
            <ProtectRoute>
              <ContractRoutes />
            </ProtectRoute>
          }
        />
        <Route path="/account/*" element={<AccountRoutes />} />
      </Routes>
      <Footer />
      {!isDesktop && (
        <div className="fixed right-0 bottom-0 left-0 z-[9999]">
          <MainNavMobile />
        </div>
      )}
    </div>
  );
}

export default App;
