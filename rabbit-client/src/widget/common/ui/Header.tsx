import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { LoginButton } from "@/entities/common";

import AlarmButton from "@/entities/common/ui/AlramButton";
import RabbitButton from "@/entities/common/ui/RabbitButton";

import MainNav from "@/features/common/ui/MainNav";
import { Link } from "react-router";

const Header = () => {
  const { isAuthenticated } = useAuthUser();

  return (
    <>
      <h1 className="sr-only">Rabbit</h1>
      <header className="mb-6 flex w-full items-center justify-between px-8 py-4 pt-10">
        <Link to="/">
          <img
            src="/logo.svg"
            alt="Rabbit"
            className="h-4 min-w-[150px] pr-5 md:h-6"
          />
        </Link>
        <div className="flex items-center">
          <MainNav /> <AlarmButton />
          {isAuthenticated ? (
            <div className="flex gap-4 px-4 pb-1.5">
              <AlarmButton />
              <RabbitButton />
            </div>
          ) : (
            <LoginButton />
          )}
        </div>
      </header>
    </>
  );
};
export default Header;
