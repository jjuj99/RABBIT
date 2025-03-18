import NavItem from "@/entities/common/ui/NavItem";
import { SUB_NAV_ITEMS } from "@/shared/constant/navigation/nav";
import { useLocation } from "react-router";

const SubNav = () => {
  const pathname = useLocation().pathname;
  const subNav =
    SUB_NAV_ITEMS[pathname.split("/")[1] as keyof typeof SUB_NAV_ITEMS];

  if (!subNav) {
    return null;
  }
  return (
    <nav className="flex gap-4 p-3">
      {subNav.map((item) => {
        return (
          <NavItem
            key={item.to}
            to={item.to}
            isActive={pathname.includes(item.to)}
            size="sub"
          >
            {item.label}
          </NavItem>
        );
      })}
    </nav>
  );
};
export default SubNav;
