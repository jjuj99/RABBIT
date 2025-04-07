// NotificationProvider.tsx
import { useState, useEffect, ReactNode } from "react";
import {
  NotificationContext,
  NotificationResponse,
} from "./NotificationContext";
import { ApiResponse } from "@/shared/type/ApiResponse";

export const NotificationProvider = ({ children }: { children: ReactNode }) => {
  const [notifications, setNotifications] = useState<NotificationResponse[]>(
    [],
  );

  useEffect(() => {
    const eventSource = new EventSource("/api/v1/sse/notifications");

    eventSource.onmessage = (event) => {
      try {
        const { status, data } = JSON.parse(
          event.data,
        ) as ApiResponse<NotificationResponse>;
        if (status === "SUCCESS" && data) {
          setNotifications((prev) => [...prev, data]);
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
