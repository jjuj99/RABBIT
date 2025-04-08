import { ApiResponse } from "@/shared/type/ApiResponse";
import { NotificationResponse } from "@/shared/lib/notification/NotificationContext";
import fetchOption from "@/shared/utils/fetchOption";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

interface ReadNotificationResponse {
  message: "알림 읽음 상태 변경 성공했습니다.";
  seq: null;
  value: null;
}

export const ReadNotificationAPI = async (
  notificationId: number,
): Promise<ApiResponse<ReadNotificationResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/notifications/${notificationId}/read
`,
    fetchOption("PATCH"),
  );
  const data = await res.json();
  return data;
};

export const GetNotificationAPI = async (): Promise<
  ApiResponse<NotificationResponse[]>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/notifications`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};
