import Web3 from "web3";

const RABBIT_TOKEN_DECIMALS = import.meta.env.VITE_RABBIT_TOKEN_DECIMALS;

const getRabbitBalance = async (walletAddress: string) => {
  const rabbitTokenAddress = import.meta.env.VITE_RABBIT_TOKEN_ADDRESS;
  // ERC20의 기본 ABI에서 balanceOf 함수만 필요
  const minimalABI = [
    {
      constant: true,
      inputs: [{ name: "_owner", type: "address" }],
      name: "balanceOf",
      outputs: [{ name: "balance", type: "uint256" }],
      type: "function",
    },
  ];

  const web3 = new Web3(window.ethereum);
  const tokenContract = new web3.eth.Contract(minimalABI, rabbitTokenAddress);
  // 잔액 조회
  const balance = await tokenContract.methods.balanceOf(walletAddress).call();
  // 실제 토큰 수량으로 변환
  const actualBalance = Number(balance) / Math.pow(10, RABBIT_TOKEN_DECIMALS);

  return actualBalance;
};

export default getRabbitBalance;
