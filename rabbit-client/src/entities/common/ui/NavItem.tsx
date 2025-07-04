import { cn } from "@/shared/lib/utils";
import { Link } from "react-router";

interface NavItemProps {
  to: string;
  children: React.ReactNode;
  isActive: boolean;
  size?: "main" | "sub";
}

const NavItem = ({ to, children, isActive, size = "main" }: NavItemProps) => {
  return (
    <Link
      to={to}
      className={cn(
        "box-content flex items-center gap-2 px-0.5 pb-1 text-nowrap",
        "border-b-2",
        size === "main" ? "text-lg" : "text-base md:text-lg",
        isActive
          ? "border-white text-white"
          : "text-text-disabled border-transparent hover:border-white",
      )}
    >
      {children}
    </Link>
  );
};

export default NavItem;
