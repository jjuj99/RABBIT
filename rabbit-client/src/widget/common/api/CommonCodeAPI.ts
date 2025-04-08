import { ApiResponse } from "@/shared/type/ApiResponse";
import { CommonCodeType } from "../types/request";
import { CommonCodeResponse } from "../types/response";
import fetchOption from "@/shared/utils/fetchOption";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

const getCommonCodeList = async (
  codeType: CommonCodeType,
): Promise<ApiResponse<CommonCodeResponse[]>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/codes/${codeType}/active`,
    fetchOption("GET"),
  );
  if (!response.ok) {
    throw new Error("Failed to fetch common code list");
  }
  return response.json();
};

export default getCommonCodeList;
