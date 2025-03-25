import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarSeparator,
  MenubarTrigger,
} from "@/shared/ui/menubar";
import RAB from "@/shared/ui/RAB";

const RabbitButton = () => {
  const { user, logout } = useAuthUser();

  const handleLogout = () => {
    console.log("로그아웃");
    logout();
    // 월렛 관련 쿼리 캐시 제거
  };
  return (
    <Menubar className="bg-transparent p-0">
      <MenubarMenu>
        <MenubarTrigger className="border-gradient bg-transparent p-2">
          <img src="/icons/Rabbit.svg" alt="옵션버튼" />
        </MenubarTrigger>
        <MenubarContent>
          <div className="px-2 py-1.5">
            <span className="font-bit">{user?.nickname}</span>
            <span className="font-bit text-xs">님</span>
          </div>
          <MenubarSeparator />
          <div className="py-2">
            <MenubarItem className="">
              <span>메뉴</span>
            </MenubarItem>
            <MenubarSeparator className="mx-2" />
            <MenubarItem className="">
              <span>메뉴2</span>
            </MenubarItem>
            <MenubarSeparator className="mx-2" />
            <MenubarItem className="">
              <RAB isColored={false} size="sm" amount={100000} />
            </MenubarItem>
          </div>
          <MenubarSeparator />
          <MenubarItem>
            <button onClick={handleLogout}>로그아웃</button>
          </MenubarItem>
        </MenubarContent>
      </MenubarMenu>
    </Menubar>
  );
};

export default RabbitButton;
