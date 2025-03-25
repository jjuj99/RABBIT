import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import {
  clearAccessToken,
  getAccessToken,
  refreshAccessToken,
} from "../utils/authUtils";
import { useAuthContext } from "./useAuthContext";
import { GetUserAPI, LogoutAPI } from "../api/authApi";
import useWalletConnection from "./useWalletConnection";

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
export const useLogout = () => {
  const queryClient = useQueryClient();
  const { mutate: logout } = useMutation({
    mutationFn: async () => {
      await LogoutAPI();
    },
    onSuccess: () => {
      clearAccessToken();

      // 상태 업데이트만 수행
      queryClient.setQueryData(["auth", "status"], {
        isAuthenticated: false,
      });
      queryClient.setQueryData(["auth", "user"], { nickname: null });
      queryClient.setQueryData(["wallet"], { address: null });
    },
    onError: (error) => {
      console.error("로그아웃 실패:", error);
    },
  });
  return { logout };
};

// 인증 상태와 사용자 정보를 모두 제공하는 편의 훅
export const useAuthUser = () => {
  const { data: authStatus, isLoading: isAuthLoading } = useAuth();
  const { data: user, isLoading: isUserLoading } = useUser();
  const { data: walletData } = useWalletConnection();
  const { logout } = useLogout();
  return {
    walletAddress: walletData?.address,
    isAuthenticated: authStatus?.isAuthenticated || false,
    user: user || null,
    isLoading: isAuthLoading || isUserLoading,
    logout,
  };
};
