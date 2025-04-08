import { ethers } from "ethers";

const addNFTToWallet = async (tokenId: string, nftImageUrl: string) => {
  const NFTAddress = import.meta.env.VITE_RABBIT_PROMISSORYNOTE_ADDRESS;
  const tokenSymbol = import.meta.env.VITE_RABBIT_TOKEN_SYMBOL;

  try {
    // 메타마스크 provider 연결
    if (typeof window.ethereum === "undefined") {
      console.error(
        "이더리움 지갑이 감지되지 않았습니다. MetaMask를 설치하세요.",
      );
      return false;
    }

    const provider = new ethers.BrowserProvider(window.ethereum);

    // 지갑 연결 확인 및 계정 접근 요청
    await provider.send("eth_requestAccounts", []);

    // 여전히 wallet_watchAsset 메소드 사용 (ethers.js에서도 직접 provider를 통해 호출)
    const wasAdded = await provider.send("wallet_watchAsset", {
      type: "ERC721",
      options: {
        address: NFTAddress,
        tokenId: tokenId.toString(),
        symbol: tokenSymbol,
        name: "RABBIT",
        image: nftImageUrl,
      },
    });

    if (wasAdded) {
      console.log("NFT가 지갑에 성공적으로 추가되었습니다!");
      return true;
    } else {
      console.log("사용자가 NFT 추가를 취소했습니다.");
      return false;
    }
  } catch (error) {
    console.error("NFT 추가 중 오류가 발생했습니다:", error);
    return false;
  }
};

export default addNFTToWallet;
