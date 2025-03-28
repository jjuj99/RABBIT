import { toast } from "sonner";
import ensureCorrectNetwork from "./ensureCorrectNetwork";

const RABBIT_TOKEN_ADDRESS = import.meta.env.VITE_RABBIT_TOKEN_ADDRESS;
const RABBIT_TOKEN_SYMBOL = import.meta.env.VITE_RABBIT_TOKEN_SYMBOL;
const RABBIT_TOKEN_DECIMALS = import.meta.env.VITE_RABBIT_TOKEN_DECIMALS;

// Rabbit토큰 지갑에 추가추가
export const handleAddToken = async ({
  onSuccess,
  onError,
  onFinally,
}: {
  onSuccess?: () => void;
  onError?: () => void;
  onFinally?: () => void;
}) => {
  console.log("handleAddToken 실행");
  try {
    // 먼저 올바른 네트워크인지 확인
    const isCorrectNetwork = await ensureCorrectNetwork();
    if (!isCorrectNetwork) {
      throw new Error("올바른 네트워크로 전환해주세요.");
    }

    // 토큰 추가 로직
    const wasAdded = await window.ethereum?.request({
      method: "wallet_watchAsset",
      params: {
        //@ts-expect-error erc20
        type: "ERC20",
        options: {
          // Sepolia 테스트넷의 토큰 주소
          address: RABBIT_TOKEN_ADDRESS,
          symbol: RABBIT_TOKEN_SYMBOL,
          decimals: RABBIT_TOKEN_DECIMALS,
        },
      },
    });
    onSuccess?.();
    if (wasAdded) {
      // localStorage.setItem("rabbitTokenAdded", "true");
      toast.success("Rabbit 토큰이 지갑에 추가되었습니다.");
      return true;
    } else {
      toast.error("토큰 추가가 취소되었습니다.");
      return false;
    }
  } catch (error) {
    if (error instanceof Error && "code" in error && error.code === 4001) {
      toast.error("토큰 추가를 취소했습니다.");
      onError?.();
      return false;
    }
    console.error("토큰 추가 중 오류 발생:", error);
    toast.error("토큰 추가에 실패했습니다: ");
    onError?.();
    return false;
  } finally {
    onFinally?.();
  }
};

export default handleAddToken;
