// NotificationProvider.tsx
import { useState, useEffect, ReactNode } from "react";
import {
  NotificationContext,
  NotificationResponse,
  NotificationEvent,
} from "./NotificationContext";
import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { getAccessToken } from "@/entities/auth/utils/authUtils";

export const NotificationProvider = ({ children }: { children: ReactNode }) => {
  const [notifications, setNotifications] = useState<NotificationResponse[]>(
    [],
  );
  const VITE_API_URL = import.meta.env.VITE_API_URL;
  const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;
  const { isAuthenticated } = useAuthUser();

  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    const accessToken = getAccessToken();
    if (!accessToken) {
      return;
    }

    const eventSource = new EventSource(
      `${VITE_API_URL}/${VITE_API_VERSION}/sse/subscribe/user?token=${accessToken}`,
    );

    eventSource.onmessage = (event) => {
      try {
        const notificationEvent = JSON.parse(event.data) as NotificationEvent;
        if (notificationEvent.data) {
          setNotifications((prev) => [...prev, notificationEvent.data]);
        }
      } catch (error) {
        console.error("SSE 데이터 파싱 오류:", error);
      }
    };

    eventSource.onerror = (error) => {
      console.error("SSE 연결 에러:", error);
      eventSource.close();
    };

    return () => eventSource.close();
  }, []);

  return (
    <NotificationContext.Provider value={{ notifications, setNotifications }}>
      {children}
    </NotificationContext.Provider>
  );
};
