// NotificationContext.ts
import { createContext } from "react";

export interface NotificationResponse {
  notificationId: number;
  type: string;
  title: string;
  content: string;
  readFlag: boolean;
  relatedId: number;
  relatedType: string;
  createdAt: string;
}

type NotificationContextType = {
  notifications: NotificationResponse[];
  setNotifications: React.Dispatch<
    React.SetStateAction<NotificationResponse[]>
  >;
};

export const NotificationContext = createContext<
  NotificationContextType | undefined
>(undefined);
