const SEPOLIA_CHAIN_ID = "0xaa36a7";
const SEPOLIA_NETWORK_PARAMS = {
  chainId: SEPOLIA_CHAIN_ID,
  chainName: "Sepolia Testnet",
  nativeCurrency: {
    name: "Sepolia ETH",
    symbol: "SEP",
    decimals: 18,
  },
  rpcUrls: ["https://rpc.sepolia.org"],
  blockExplorerUrls: ["https://sepolia.etherscan.io"],
};

const LOGIN_MESSAGE = (walletAddres: string, nonce: string) => {
  return `Rabbit에 오신 것을 환영합니다!
안전한 계정 로그인을 위해 이 메시지에 서명해 주세요.
이 서명은 블록체인 트랜잭션을 발생시키거나 가스 비용이 들지 않습니다.

지갑 주소: ${walletAddres}
Nonce: ${nonce}
네트워크: Sepolia Testnet
타임스탬프: ${new Date().toISOString()}`;
};

export { SEPOLIA_CHAIN_ID, SEPOLIA_NETWORK_PARAMS, LOGIN_MESSAGE };
