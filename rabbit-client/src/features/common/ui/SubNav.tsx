import NavItem from "@/entities/common/ui/NavItem";
import { useLocation } from "react-router";

interface subNavType {
  label: string;
  to: string;
}
const SubNav = ({ subNav }: { subNav: subNavType[] }) => {
  const pathname = useLocation().pathname;
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
