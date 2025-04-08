import useGetNonce from "@/entities/auth/hooks/useGetNonce";
import { useEffect, useState } from "react";

import { useMetaMaskInstalled } from "@/entities/wallet/hooks/useMetaMaskInstalled";
import checkRabbitTokenRegister from "@/entities/wallet/utils/checkRabbitTokenRegister";
import connectWallet from "@/entities/wallet/utils/connectWallet";
import ensureCorrectNetwork from "@/entities/wallet/utils/ensureCorrectNetwork";
import generateSignature from "@/entities/wallet/utils/generateSignature";
import getMetaMaskProvider from "@/entities/wallet/utils/getMetaMaskProvider";
import { cn } from "@/shared/lib/utils";
import { toast } from "sonner";

import { LoginAPI } from "@/entities/auth/api/authApi";
import { setAccessToken } from "@/entities/auth/utils/authUtils";
import { LOGIN_MESSAGE } from "@/entities/wallet/constant";
import { useQueryClient } from "@tanstack/react-query";

import SignupDialog from "@/features/auth/ui/SignupDialog";

const LoginButton = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [isSignupDialogOpen, setIsSignupDialogOpen] = useState(false);

  const {
    isInstalled,
    isLoading: isMetaMaskLoading,
    error: metaMaskError,
  } = useMetaMaskInstalled();

  useEffect(() => {
    if (metaMaskError === "NOT_INSTALLED") {
      toast.error("메타마스크가 설치되어 있지 않습니다.");
    } else if (metaMaskError === "CHECK_FAILED") {
      toast.error("메타마스크 상태 확인에 실패했습니다.");
    }
  }, [metaMaskError]);

  const nonceMutation = useGetNonce();
  const queryClient = useQueryClient();

  const handleLogin = async () => {
    setIsLoading(true);

    // 1. 메타마스크 설치 여부 체크 - 없으면 예외 처리 toast 띄우기
    if (!isInstalled) {
      setIsLoading(false);
      return;
    }

    // 2. 네트워크 체크 -> Sepolia 네트워크 여부 체크 - 아니면 예외 처리 toast 띄우기
    const { success, error: networkError } = await ensureCorrectNetwork();
    if (!success) {
      switch (networkError) {
        case "USER_REJECTED_SWITCH":
          toast.error(
            "네트워크 전환이 필요합니다. Sepolia 테스트넷으로 전환해주세요.",
          );
          break;
        case "USER_REJECTED_ADD":
          toast.error("Sepolia 테스트넷 추가가 필요합니다.");
          break;
        case "NETWORK_ERROR":
          toast.error("네트워크 연결에 실패했습니다.");
          break;
      }
      setIsLoading(false);
      return;
    }

    // 토큰 등록 확인 및 등록 시도
    try {
      const { isRegistered, error: tokenError } =
        await checkRabbitTokenRegister();
      if (!isRegistered && tokenError) {
        toast.error(tokenError);
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      }
    }

    // 3. 지갑 연결 요청 - 예외 처리 toast 띄우기, 지갑 연결을 거부했을 때?
    // 4. 지갑 주소 반환 - 예외 처리 toast 띄우기
    const provider = await getMetaMaskProvider();
    if (!provider) {
      toast.error("메타마스크 프로바이더를 찾을 수 없습니다.");
      setIsLoading(false);
      return;
    }
    const { address, error: connectError } = await connectWallet({ provider });
    if (!address) {
      switch (connectError) {
        case "PROVIDER_NOT_FOUND":
          toast.error("메타마스크 프로바이더를 찾을 수 없습니다.");
          break;
        case "USER_REJECTED":
          toast.error("지갑 연결을 거부했습니다.");
          break;
        case "CONNECTION_ERROR":
          toast.error("지갑 연결에 실패했습니다.");
          break;
      }
      setIsLoading(false);
      return;
    }

    // 6. 난수 요청

    const { data: nonce } = await nonceMutation.mutateAsync(address);
    console.log(nonce);
    if (!nonce) {
      setIsSignupDialogOpen(true);
      setIsLoading(false);
      return;
    }

    // 7. 시그니처 생성

    const { signature, error: signatureError } = await generateSignature(
      address,
      LOGIN_MESSAGE(address, nonce.nonce),
    );
    console.log(signature);

    if (!signature) {
      switch (signatureError) {
        case "PROVIDER_NOT_FOUND":
          toast.error("메타마스크 프로바이더를 찾을 수 없습니다.");
          break;
        case "USER_REJECTED":
          toast.error("서명을 거부했습니다.");
          break;
        case "SIGNATURE_FAILED":
          toast.error("서명 생성에 실패했습니다.");
          break;
      }
      setIsLoading(false);
      return;
    }
    // 8. 시그니처 전송 - 백엔드에서 시그니처 검증
    try {
      const loginRes = await LoginAPI({
        walletAddress: address,
        signature,
        nonce: LOGIN_MESSAGE(address, nonce.nonce),
      });

      if (loginRes.status === "SUCCESS" && loginRes.data) {
        toast.success("로그인에 성공했습니다.");
        setAccessToken(loginRes.data.accessToken);

        queryClient.setQueryData(["user"], {
          isAuthenticated: true,
          user: {
            nickname: loginRes.data.nickname,
            userName: loginRes.data.userName,
          },
        });
        setIsLoading(false);
        return;
      } else {
        if (loginRes.status === "ERROR") {
          toast.error(loginRes.error?.message);
          setIsLoading(false);
          return;
        }
      }
    } catch {
      toast.error("로그인에 실패했습니다.");
      setIsLoading(false);
      return;
    }
  };

  if (isMetaMaskLoading) {
    return <div>확인 중...</div>;
  }

  return (
    <>
      <div className="flex flex-col items-center">
        <button
          className={cn(
            "cursor-pointer rounded-md px-4 pb-1.5 text-xl text-nowrap",
            isLoading ? "opacity-50" : "",
          )}
          onClick={handleLogin}
          disabled={isLoading}
        >
          {isLoading ? "로그인 중..." : "로그인"}
        </button>
      </div>

      <SignupDialog
        isOpen={isSignupDialogOpen}
        onOpenChange={setIsSignupDialogOpen}
      />
    </>
  );
};

export default LoginButton;
