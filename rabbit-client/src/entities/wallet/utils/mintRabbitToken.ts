import Web3 from "web3";
import getMetaMaskProvider from "./getMetaMaskProvider";
import getWalletAddress from "./getWalletAddress";

// 민팅을 위한 최소한의 ABI
const MINTER_ABI = [
  {
    inputs: [
      { name: "to", type: "address" },
      { name: "amount", type: "uint256" },
    ],
    name: "mint",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
];

const mintRabbitToken = async (amount: number): Promise<boolean> => {
  try {
    const provider = await getMetaMaskProvider();
    if (!provider) {
      throw new Error("MetaMask provider를 찾을 수 없습니다.");
    }

    const { address } = await getWalletAddress({ provider });
    if (!address) {
      throw new Error("지갑 주소를 찾을 수 없습니다.");
    }

    const web3 = new Web3(provider);
    const rabbitTokenAddress = import.meta.env.VITE_RABBIT_TOKEN_ADDRESS;
    const contract = new web3.eth.Contract(MINTER_ABI, rabbitTokenAddress);

    // 토큰 단위 변환 (decimals 고려)
    const decimals = Number(import.meta.env.VITE_RABBIT_TOKEN_DECIMALS);
    const amountInWei = BigInt(amount * Math.pow(10, decimals));

    // 민팅 트랜잭션 실행
    await contract.methods.mint(address, amountInWei.toString()).send({
      from: address,
    });

    return true;
  } catch (error) {
    console.error("민팅 실패:", error);
    throw error;
  }
};

export default mintRabbitToken;
