import { useState } from "react";
import { LoginAPI } from "@/entities/auth/api/authApi";
import useGetNonce from "@/entities/auth/hooks/useGetNonce";
import useWalletConnection from "@/entities/auth/hooks/useWalletConnection";
import { setAccessToken } from "@/entities/auth/utils/authUtils";
import { useQueryClient } from "@tanstack/react-query";

const LoginButton = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const walletQuery = useWalletConnection();
  const nonceMutation = useGetNonce();
  const queryClient = useQueryClient();

  // 로그인 프로세스 : 지갑연결 -> 지갑주소 전송 -> 난수 생성 -> 시그니처 생성 -> 시그니처 전송 -> 백엔드에서 시그니처 검증 -> 로그인 성공 -> 유저 정보 받아옴
  const handleLogin = async () => {
    try {
      setIsLoading(true);
      setError(null);

      // 1. 지갑 연결 및 주소 가져오기
      const { data: walletData } = await walletQuery.refetch();
      if (!walletData?.address) {
        setError("지갑 연결에 실패했습니다. 다시 시도해주세요.");
        return;
      }

      // 2. 난수(nonce) 요청
      const { data: nonceResponse } = await nonceMutation.mutateAsync(
        walletData.address,
      );

      // 3. 회원가입이 필요한 경우 처리
      if (!nonceResponse) {
        console.log("회원가입이 필요합니다.");
        return;
      }

      // 4. 메시지 서명 요청
      if (window.ethereum) {
        // 서명할 메시지 생성
        const message = `Rabbit에 오신 것을 환영합니다!
안전한 계정 로그인을 위해 이 메시지에 서명해 주세요.
이 서명은 블록체인 트랜잭션을 발생시키거나 가스 비용이 들지 않습니다.

지갑 주소: ${walletData.address}
Nonce: ${nonceResponse.nonce}
타임스탬프: ${new Date().toISOString()}`;

        // 지갑에 서명 요청
        const signature = await window.ethereum.request({
          method: "personal_sign",
          params: [message, walletData.address],
        });

        // 5. 로그인 API 호출
        const loginRes = await LoginAPI({
          walletAddress: walletData.address,
          signature: signature as string,
          nonce: nonceResponse.nonce,
        });

        // 6. 로그인 성공 처리
        if (loginRes.status === "SUCCESS" && loginRes.data) {
          // 액세스 토큰 저장
          setAccessToken(loginRes.data.accessToken);

          // 인증 상태와 사용자 정보를 분리하여 저장

          // 1. 인증 상태만 저장 (isAuthenticated: true)
          queryClient.setQueryData(["auth", "status"], {
            isAuthenticated: true,
          });

          // 2. 사용자 정보 저장
          queryClient.setQueryData(["auth", "user"], loginRes.data.user);

          console.log("로그인 성공!");
        } else {
          setError("로그인에 실패했습니다. 다시 시도해주세요.");
        }
      } else {
        setError("Ethereum 지원 브라우저가 필요합니다.");
      }
    } catch (error: unknown) {
      console.error("로그인 오류:", error);
      setError(
        error instanceof Error
          ? error.message
          : "로그인 중 오류가 발생했습니다.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      className={`cursor-pointer ${isLoading ? "opacity-50" : ""}`}
      onClick={handleLogin}
      disabled={isLoading}
    >
      {isLoading ? "로그인 중..." : "로그인"}
    </button>
  );
};

export default LoginButton;
