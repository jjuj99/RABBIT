import { cn } from "@/shared/lib/utils";
import { Link } from "react-router";

interface NavItemMobileProps {
  to: string;
  children: React.ReactNode;
  isActive: boolean;
  icon: string;
}

const NavItemMobile = ({
  to,
  children,
  isActive,
  icon,
}: NavItemMobileProps) => {
  return (
    <Link
      to={to}
      className={cn(
        "box-content flex w-full flex-col items-center justify-center gap-2 bg-gray-900 pt-4 pb-3 text-sm",
        "border-t-2",
        isActive
          ? "border-gray-200 text-white"
          : "border-transparent text-gray-200 hover:border-white",
      )}
    >
      <img src={icon} alt={typeof children === "string" ? children : ""} />
      {children}
    </Link>
  );
};

export default NavItemMobile;
