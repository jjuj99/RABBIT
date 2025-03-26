import NavItem from "@/entities/common/ui/NavItem";
import { NAV_ITEMS } from "@/shared/constant/navigation/nav";
import { useLocation } from "react-router";

const MainNav = () => {
  const pathname = useLocation().pathname;
  console.log(pathname);

  return (
    <nav className="flex gap-8">
      {NAV_ITEMS.map((item) => (
        <NavItem
          key={item.to}
          to={item.to}
          isActive={pathname.includes(item.activeLabel)}
        >
          {item.label}
        </NavItem>
      ))}
    </nav>
  );
};

export default MainNav;
