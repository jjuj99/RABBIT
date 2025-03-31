import { ApiResponse } from "@/shared/type/ApiResponse";
import fetchOption from "@/shared/utils/fetchOption";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

interface RequestConfirm {
  orderId: string | null;
  amount: string | null;
  paymentKey: string | null;
}
interface ResponseConfirm {
  message: string | null;
}
export const ConfirmAPI = async (
  confirmData: RequestConfirm,
): Promise<ApiResponse<ResponseConfirm>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/coins/confirm`,
    fetchOption("POST", confirmData, "access"),
  );
  const data = await res.json();
  return data;
};
