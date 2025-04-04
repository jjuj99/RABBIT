import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { LoginButton } from "@/entities/common";

import AlarmButton from "@/entities/common/ui/AlramButton";
import RabbitButton from "@/entities/common/ui/RabbitButton";
import { Separator } from "@/shared/ui/Separator";

import { Link } from "react-router";

const HeaderMobile = () => {
  const { isAuthenticated } = useAuthUser();

  return (
    <>
      <h1 className="sr-only">Rabbit</h1>
      <div>
        <header className="flex w-full items-center justify-between px-8 py-4">
          <Link to="/">
            <img src="/logo.svg" alt="Rabbit" className="h-5 w-[130px]" />
          </Link>
          <div className="flex items-center gap-20">
            {isAuthenticated ? (
              <div className="flex gap-4">
                <AlarmButton />
                <RabbitButton />
              </div>
            ) : (
              <LoginButton />
            )}
          </div>
        </header>
        <Separator className="mb-6" />
      </div>
    </>
  );
};
export default HeaderMobile;
