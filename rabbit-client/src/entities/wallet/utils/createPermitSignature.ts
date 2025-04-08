import { ethers } from "ethers";
import tokenABI from "@/shared/lib/web3/ABI/tokenABI.json";

export const createPermitSignature = async (
  tokenAddress: string,
  owner: string,
  spender: string,
  value: string,
  deadline: number,
): Promise<string> => {
  if (!window.ethereum) {
    throw new Error("메타마스크가 필요합니다");
  }

  // ethers v6에 맞게 업데이트
  const provider = new ethers.BrowserProvider(window.ethereum);
  const signer = await provider.getSigner();
  const chainId = (await provider.getNetwork()).chainId;

  // 토큰 컨트랙트에서 nonce 조회
  const tokenContract = new ethers.Contract(tokenAddress, tokenABI, provider);
  const nonce = await tokenContract.nonces(owner);

  // EIP-712 도메인
  const domain = {
    name: "RABBIT",
    version: "1",
    chainId,
    verifyingContract: tokenAddress,
  };

  // 타입
  const types = {
    Permit: [
      { name: "owner", type: "address" },
      { name: "spender", type: "address" },
      { name: "value", type: "uint256" },
      { name: "nonce", type: "uint256" },
      { name: "deadline", type: "uint256" },
    ],
  };

  // 값
  const value712 = {
    owner,
    spender,
    value,
    nonce: nonce.toString(),
    deadline,
  };

  // 서명 요청 (ethers v6에 맞게 업데이트)
  const signature = await signer.signTypedData(domain, types, value712);
  return signature;
};
