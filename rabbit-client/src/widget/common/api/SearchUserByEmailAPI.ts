import { ApiResponse } from "@/shared/type/ApiResponse";
import fetchOption from "@/shared/utils/fetchOption";
import { SearchUserResponse } from "../types/response";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const SearchUserByEmailAPI = async (
  email: string,
): Promise<ApiResponse<SearchUserResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/users?searchEmail=${email}`,
    fetchOption("GET", undefined, "access"),
  );
  const data = await res.json();
  return data;
};
