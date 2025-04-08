// NotificationProvider.tsx
import { useState, useEffect, ReactNode } from "react";
import {
  NotificationContext,
  NotificationResponse,
  NotificationEvent,
} from "./NotificationContext";

export const NotificationProvider = ({ children }: { children: ReactNode }) => {
  const [notifications, setNotifications] = useState<NotificationResponse[]>(
    [],
  );

  useEffect(() => {
    const eventSource = new EventSource("/subscribe?type=user");

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
