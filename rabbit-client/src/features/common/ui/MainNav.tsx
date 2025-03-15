import NavItem from "@/entities/common/ui/NavItem";
import { useLocation } from "react-router";

const MainNav = () => {
  const NAV_ITEMS = [
    {
      to: "/contract/new",
      label: "차용증 작성",
    },
    {
      to: "/auction/list",
      label: "차용증 경매",
    },
    {
      to: "/loan/borrow",
      label: "채권\u2022채무",
    },
    {
      to: "/account/charge",
      label: "입\u2022출금",
    },
  ];
  const pathname = useLocation().pathname;
  console.log(pathname);

  return (
    <nav className="flex gap-8">
      {NAV_ITEMS.map((item) => (
        <NavItem
          key={item.to}
          to={item.to}
          isActive={pathname.includes(item.to)}
        >
          {item.label}
        </NavItem>
      ))}
    </nav>
  );
};

export default MainNav;
