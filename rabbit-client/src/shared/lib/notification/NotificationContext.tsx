// NotificationContext.ts
import { createContext } from "react";

export interface NotificationResponse {
  notificationId: number;
  type: NotificationType;
  title: string;
  content: string;
  readFlag: boolean;
  relatedId: number;
  relatedType: string;
  createdAt: string;
}

export type NotificationType =
  | "AUCTION_FAILED"
  | "AUCTION_SUCCESS"
  | "AUCTION_TRANSFERRED"
  | "BID_FAILED";

export interface NotificationEvent {
  id: string;
  event: string;
  data: NotificationResponse;
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
