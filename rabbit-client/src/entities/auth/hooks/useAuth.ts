import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import {
  clearAccessToken,
  getAccessToken,
  refreshAccessToken,
} from "../utils/authUtils";
import { useAuthContext } from "./useAuthContext";
import { GetUserAPI, LogoutAPI } from "../api/authApi";

import { toast } from "sonner";

type UserState = {
  isAuthenticated: boolean;
  user: {
    nickname: string;
    userName: string;
    bankId: number;
    bankName: string;
    refundAccount: string;
    email: string;
  } | null;
};

type DecodedToken = {
  exp: number;
  iat: number;
  sub: string;
  userId: string;
};

// 통합된 인증/유저 정보 훅
export const useAuth = () => {
  const { isInitialized } = useAuthContext();

  return useQuery({
    queryKey: ["user"],
    queryFn: async (): Promise<UserState> => {
      try {
        const token = getAccessToken();
        if (!token) {
          return { isAuthenticated: false, user: null };
        }

        const decoded = jwtDecode<DecodedToken>(token);
        if (decoded.exp * 1000 < Date.now()) {
          const refreshResponse = await refreshAccessToken();
          if (!refreshResponse) {
            return { isAuthenticated: false, user: null };
          }
        }

        // userId가 있으면 유저 정보 조회
        if (decoded.userId) {
          const response = await GetUserAPI();
          if (!response.data) {
            return { isAuthenticated: false, user: null };
          }
          return {
            isAuthenticated: true,
            user: response.data,
          };
        }

        return { isAuthenticated: false, user: null };
      } catch (error) {
        console.log("Auth check failed:", error);
        return { isAuthenticated: false, user: null };
      }
    },
    enabled: isInitialized,
    refetchOnWindowFocus: true,
    retry: false,
    staleTime: 5 * 60 * 1000, // 5분
  });
};

// 사용자 정보를 가져오는 훅
export const useUser = () => {
  const { data: authStatus } = useAuth();
  console.log(authStatus);
  const isAuthenticated = authStatus?.isAuthenticated || false;

  return useQuery({
    queryKey: ["user"],
    queryFn: async () => {
      // API에서 사용자 정보 가져오기
      // 이미 쿼리 클라이언트에 캐시되어 있으면 이 함수는 실행되지 않음
      const response = await GetUserAPI();

      return response.data;
    },
    enabled: isAuthenticated, // 인증된 경우에만 실행
    staleTime: 5 * 60 * 1000, // 5분 동안 캐시 유지
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();
  const { mutate: logout } = useMutation({
    mutationFn: LogoutAPI,
    onSuccess: () => {
      clearAccessToken();
      localStorage.removeItem("nextTokenDialogShowTime");

      // 단일 쿼리로 상태 업데이트
      queryClient.setQueryData(["user"], {
        isAuthenticated: false,
        user: null,
      });

      toast.success("로그아웃 되었습니다.");
    },
    onError: () => {
      toast.error("로그아웃에 실패했습니다.");
    },
  });
  return { logout };
};

// 인증 상태와 사용자 정보를 모두 제공하는 편의 훅
export const useAuthUser = () => {
  const { data: authData, isLoading } = useAuth();
  const { logout } = useLogout();

  return {
    isAuthenticated: authData?.isAuthenticated || false,
    user: authData?.user || null,
    isLoading,
    logout,
  };
};
