import NavItem from "@/entities/common/ui/NavItem";
import { SUB_NAV_ITEMS } from "@/shared/constant/navigation/nav";
import { cn } from "@/shared/lib/utils";
import { useLocation } from "react-router";

interface SubNavProps {
  className?: string;
}

const SubNav = ({ className }: SubNavProps) => {
  const pathname = useLocation().pathname;
  const subNav =
    SUB_NAV_ITEMS[pathname.split("/")[1] as keyof typeof SUB_NAV_ITEMS];

  if (!subNav) {
    return null;
  }
  return (
    <nav className={cn("mb-6 flex gap-4 p-3", className)}>
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
