import { useQuery } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import { getAccessToken, refreshAccessToken } from "../utils/authUtils";
import { useAuthContext } from "./useAuthContext";
import { GetUserAPI } from "../api/authApi";

// 인증 상태를 확인하는 훅
export const useAuth = () => {
  const { isInitialized } = useAuthContext();

  return useQuery({
    queryKey: ["auth", "status"],
    queryFn: async () => {
      // 액세스 토큰 확인
      const token = getAccessToken();

      // 토큰이 없는 경우 - 인증되지 않은 상태
      if (!token) {
        return { isAuthenticated: false };
      }

      try {
        // 토큰 검증 (만료 여부 확인)
        // const decoded = jwtDecode(token);
        const decoded = { exp: 5 * 60 };

        // 토큰이 만료된 경우 갱신 시도
        if (decoded.exp && decoded.exp * 1000 < Date.now()) {
          const refreshResponse = await refreshAccessToken();

          // 갱신 성공
          if (refreshResponse) {
            return { isAuthenticated: true };
          }
          // 갱신 실패
          else {
            return { isAuthenticated: false };
          }
        }

        // 유효한 토큰
        return { isAuthenticated: true };
      } catch (error) {
        console.log("Token validation failed:", error);
        return { isAuthenticated: false };
      }
    },
    enabled: isInitialized,
    refetchOnWindowFocus: true,
    retry: false,
  });
};

// 사용자 정보를 가져오는 훅
export const useUser = () => {
  const { data: authStatus } = useAuth();
  const isAuthenticated = authStatus?.isAuthenticated || false;

  return useQuery({
    queryKey: ["auth", "user"],
    queryFn: async () => {
      // API에서 사용자 정보 가져오기
      // 이미 쿼리 클라이언트에 캐시되어 있으면 이 함수는 실행되지 않음
      const response = await GetUserAPI();
      console.log("response", response);

      return response.data;
    },
    enabled: isAuthenticated, // 인증된 경우에만 실행
    staleTime: 5 * 60 * 1000, // 5분 동안 캐시 유지
  });
};

// 인증 상태와 사용자 정보를 모두 제공하는 편의 훅
export const useAuthUser = () => {
  const { data: authStatus, isLoading: isAuthLoading } = useAuth();
  const { data: user, isLoading: isUserLoading } = useUser();

  return {
    isAuthenticated: authStatus?.isAuthenticated || false,
    user: user || null,
    isLoading: isAuthLoading || isUserLoading,
  };
};
