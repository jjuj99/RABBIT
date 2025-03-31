import { useQuery } from "@tanstack/react-query";
import { SearchUserByEmailAPI } from "../api/SearchUserByEmailAPI";
import { SearchUserResponse } from "../types/response";

const useSearchUserByEmail = (email: string) => {
  return useQuery<SearchUserResponse>({
    queryKey: ["searchUserByEmail", email],
    queryFn: async () => {
      const response = await SearchUserByEmailAPI(email);
      if (response.status === "SUCCESS" && response.data) {
        return response.data;
      }
      throw new Error(response.error?.message || "검색에 실패했습니다");
    },
    enabled: false,
    staleTime: 0,
  });
};

export default useSearchUserByEmail;
