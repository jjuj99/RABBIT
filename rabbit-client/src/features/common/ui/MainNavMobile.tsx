import NavItemMobile from "@/entities/common/ui/NavItemMobile";
import { MobileNAV_ITEMS } from "@/shared/constant/navigation/nav";
import { useLocation } from "react-router";

const MainNavMobile = () => {
  const pathname = useLocation().pathname;
  console.log(pathname);

  console.log("here", pathname);

  return (
    <nav className="flex w-full items-center">
      {MobileNAV_ITEMS.map((item) => (
        <div key={item.to} className="w-full">
          <NavItemMobile
            to={item.to}
            isActive={
              item.to === "/"
                ? pathname === "/"
                : pathname.includes(item.activeLabel)
            }
            icon={item.icon}
          >
            {item.label}
          </NavItemMobile>
        </div>
      ))}
    </nav>
  );
};

export default MainNavMobile;
