import { cn } from "@/shared/lib/utils";
import { NavLink } from "react-router";

const AccountNav = () => {
  return (
    <div className="flex gap-2">
      <NavLink
        to="/account/charge"
        className={({ isActive }) =>
          cn(
            "w-full rounded-sm py-2 text-center text-lg font-medium md:text-xl",
            isActive ? "bg-brand-primary text-black" : "bg-gray-600 text-white",
          )
        }
      >
        충전
      </NavLink>
      <NavLink
        to="/account/withdraw"
        className={({ isActive }) =>
          cn(
            "w-full rounded-sm py-2 text-center text-lg font-medium md:text-xl",
            isActive ? "bg-brand-primary text-black" : "bg-gray-600 text-white",
          )
        }
      >
        출금
      </NavLink>
    </div>
  );
};

export default AccountNav;
