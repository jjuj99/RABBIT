import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarTrigger,
} from "@/shared/ui/menubar";

const AlarmButton = () => {
  return (
    <Menubar className="border-none bg-transparent p-0">
      <MenubarMenu>
        <MenubarTrigger className="border-gradient bg-transparent p-2">
          <img src="/icons/bell.svg" alt="알림버튼" />
        </MenubarTrigger>
        <MenubarContent>
          <MenubarItem>1</MenubarItem>
          <MenubarItem>2</MenubarItem>
        </MenubarContent>
      </MenubarMenu>
    </Menubar>
  );
};

export default AlarmButton;
