import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { refreshAccessToken } from "../utils/authUtils";
import { AuthContext } from "../context/AuthContext";

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const queryClient = useQueryClient();
  const [isLoading, setIsLoading] = useState(true);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    const initAuth = async () => {
      try {
        const authResponse = await refreshAccessToken();

        if (authResponse) {
          // 인증 상태와 유저 정보를 하나의 쿼리로 통합
          queryClient.setQueryData(["user"], {
            isAuthenticated: true,
            user: {
              userName: authResponse.userName,
              nickname: authResponse.nickname,
              email: authResponse.email,
              bankId: authResponse.bankId,
              bankName: authResponse.bankName,
              refundAccount: authResponse.refundAccount,
            },
          });
        } else {
          // 인증 실패 시
          queryClient.setQueryData(["user"], {
            isAuthenticated: false,
            user: null,
          });
        }
      } catch (error) {
        console.error("인증 초기화 중 오류:", error);

        // 오류 발생 시 비인증 상태로 설정
        queryClient.setQueryData(["user"], {
          isAuthenticated: false,
          user: null,
        });
      } finally {
        setIsInitialized(true);
        setIsLoading(false);
      }
    };

    initAuth();
  }, [queryClient]);

  if (isLoading) {
    return <div className="loader-sprite" />;
  }

  return (
    <AuthContext.Provider value={{ isInitialized }}>
      {children}
    </AuthContext.Provider>
  );
};
