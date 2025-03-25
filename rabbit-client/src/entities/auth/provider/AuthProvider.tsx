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
        // 리프레시 토큰으로 인증 시도
        const authResponse = await refreshAccessToken();

        if (authResponse) {
          // 인증 성공 시 두 쿼리를 분리해서 저장

          // 1. 인증 상태만 저장
          queryClient.setQueryData(["auth", "status"], {
            isAuthenticated: true,
          });

          // 2. 사용자 정보만 저장
          queryClient.setQueryData(["auth", "user"], authResponse.user);
        } else {
          // 인증 실패 시
          queryClient.setQueryData(["auth", "status"], {
            isAuthenticated: false,
          });

          // 사용자 데이터 제거
          queryClient.removeQueries({ queryKey: ["auth", "user"] });
        }
      } catch (error) {
        console.error("인증 초기화 중 오류:", error);

        // 오류 발생 시 비인증 상태로 설정
        queryClient.setQueryData(["auth", "status"], {
          isAuthenticated: false,
        });

        // 사용자 데이터 제거
        queryClient.removeQueries({ queryKey: ["auth", "user"] });
      } finally {
        // 초기화 완료
        setIsInitialized(true);
        setIsLoading(false);
      }
    };

    initAuth();
  }, [queryClient]);

  if (isLoading) {
    return <div>인증 중</div>;
  }

  return (
    <AuthContext.Provider
      value={{
        isInitialized,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
