import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarTrigger,
} from "@/shared/ui/menubar";
import RAB from "@/shared/ui/RAB";
import { Separator } from "@radix-ui/react-menubar";

const RabbitButton = () => {
  const { user, logout } = useAuthUser();

  console.log(user);

  const handleLogout = () => {
    logout();
    // 월렛 관련 쿼리 캐시 제거
  };

  const { balance } = useGetBalance();
  console.log(balance);

  return (
    <Menubar className="bg-transparent p-0">
      <MenubarMenu>
        <MenubarTrigger className="border-gradient bg-transparent p-2">
          <img src="/icons/Rabbit.svg" alt="옵션버튼" />
        </MenubarTrigger>
        <MenubarContent>
          <div className="px-2 py-1.5">
            <span className="font-bit">{user?.user.nickname}</span>
            <span className="font-bit text-xs">님</span>
          </div>
          <Separator />
          <div className="py-2">
            <MenubarItem className="">
              <span>메뉴</span>
            </MenubarItem>
            <Separator />
            <MenubarItem className="">
              <span>메뉴2</span>
            </MenubarItem>
            <Separator />
            <MenubarItem className="">
              <RAB isColored={false} size="sm" amount={balance} />
            </MenubarItem>
          </div>
          <Separator />
          <MenubarItem>
            <button onClick={handleLogout}>로그아웃</button>
          </MenubarItem>
        </MenubarContent>
      </MenubarMenu>
    </Menubar>
  );
};

export default RabbitButton;
