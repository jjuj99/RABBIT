import {
  NotificationContext,
  NotificationResponse,
} from "@/shared/lib/notification/NotificationContext.tsx";
import {
  Menubar,
  MenubarContent,
  MenubarItem,
  MenubarMenu,
  MenubarTrigger,
} from "@/shared/ui/menubar";
import { Separator } from "@/shared/ui/Separator";
import { ScrollArea } from "@/shared/ui/scroll-area";
import { useContext, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/shared/ui/dialog";
import { Button } from "@/shared/ui/button";
import { useNavigate } from "react-router";
import { ReadNotificationAPI } from "@/widget/common/api/NotificationAPI";

const AlarmButton = () => {
  const context = useContext(NotificationContext);
  const notifications = context?.notifications || [];
  const unreadCount = notifications.filter((n) => !n.readFlag).length;
  const [selectedNotification, setSelectedNotification] =
    useState<NotificationResponse | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  const handleNotificationClick = (notification: NotificationResponse) => {
    setSelectedNotification(notification);
    setIsModalOpen(true);
  };

  const handleDetailView = () => {
    if (!selectedNotification) return;
    ReadNotificationAPI(selectedNotification.notificationId);
    switch (selectedNotification.type) {
      case "AUCTION_FAILED":
        navigate(`/loan/lent`);
        break;
      case "AUCTION_SUCCESS":
        navigate(`/loan/lent`);
        break;
      case "AUCTION_TRANSFERRED":
        navigate(`/loan/lent`);
        break;
      case "BID_FAILED":
        navigate(`/auction/${selectedNotification.relatedId}`);
        break;
    }
    setIsModalOpen(false);
  };

  const handleDeleteNotification = () => {
    if (!selectedNotification) return;
    ReadNotificationAPI(selectedNotification.notificationId);
    setIsModalOpen(false);
  };

  return (
    <>
      <Menubar className="border-none bg-transparent p-0">
        <MenubarMenu>
          <MenubarTrigger className="border-gradient relative h-9 w-9 bg-transparent p-2">
            <img src="/icons/bell.svg" alt="알림버튼" />
            {unreadCount > 0 && (
              <div className="absolute -top-1 -right-2 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-xs text-white">
                {unreadCount}
              </div>
            )}
          </MenubarTrigger>
          <MenubarContent>
            {notifications.length === 0 ? (
              <MenubarItem disabled>새로운 알림이 없습니다.</MenubarItem>
            ) : (
              <ScrollArea className="h-[300px] w-full">
                {notifications.map((notification, index) => (
                  <>
                    <MenubarItem
                      key={notification.notificationId}
                      className="flex cursor-pointer flex-col justify-start gap-1"
                      onClick={() => handleNotificationClick(notification)}
                    >
                      <div className="flex w-full flex-col gap-1 py-1">
                        <div className="flex flex-row items-center justify-between">
                          <p className="font-semibold">{notification.title}</p>
                          <p className="text-xs font-light text-gray-200">
                            경매 번호 {notification.relatedId}
                          </p>
                        </div>
                        <p className="text-sm text-gray-100">
                          {notification.content}
                        </p>
                        <p className="text-xs font-light text-gray-200">
                          {new Date(notification.createdAt).toLocaleString()}
                        </p>
                      </div>
                    </MenubarItem>
                    {index !== notifications.length - 1 && (
                      <Separator className="h-[1px] w-full" />
                    )}
                  </>
                ))}
              </ScrollArea>
            )}
          </MenubarContent>
        </MenubarMenu>
      </Menubar>

      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{selectedNotification?.title}</DialogTitle>
            <DialogDescription className="sr-only">
              알림 상세 내용을 확인하고 관련 페이지로 이동할 수 있습니다.
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-4">
            <p>{selectedNotification?.content}</p>
            <p className="text-sm text-gray-400">
              {selectedNotification &&
                new Date(selectedNotification.createdAt).toLocaleString()}
            </p>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={handleDeleteNotification}>
                알림 지우기
              </Button>
              <Button onClick={handleDetailView}>상세 확인</Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default AlarmButton;
