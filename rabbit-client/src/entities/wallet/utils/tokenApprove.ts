import { ethers } from "ethers";
import tokenABI from "@/shared/lib/web3/ABI/tokenABI.json";
import { toast } from "sonner";
const tokenApprove = async (owner: string, amount: string) => {
  if (!window.ethereum) {
    throw new Error("메타마스크가 필요합니다");
  }
  const tokenAddress = import.meta.env.VITE_RABBIT_TOKEN_ADDRESS;
  const provider = new ethers.BrowserProvider(window.ethereum);
  const signer = await provider.getSigner();
  const tokenContract = new ethers.Contract(
    tokenAddress,
    tokenABI, // 전체 ABI 사용
    signer,
  );
  const approveAmount = amount; // 1 TOKEN (18 decimals)
  console.log(`${owner}에게 ${approveAmount} 토큰 승인 중...`);
  const approveTx = await tokenContract.approve(owner, approveAmount);
  console.log("승인 트랜잭션 전송됨:", approveTx.hash);
  const receipt = await approveTx.wait();
  console.log("승인 트랜잭션 확인됨:", receipt);

  toast.success("토큰 승인이 완료되었습니다");
};

export default tokenApprove;
