// hardhat.config.js
require("@nomicfoundation/hardhat-toolbox");
require("dotenv").config();

// .env 파일에서 개인키와 API 키를 가져옵니다
const PRIVATE_KEY = process.env.PRIVATE_KEY || "";
const INFURA_API_KEY = process.env.INFURA_API_KEY || "";
const ETHERSCAN_API_KEY = process.env.ETHERSCAN_API_KEY || "";

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: {
    version: "0.8.28",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200
      },
      viaIR: true
    }
  },
  networks: {
    // 로컬 개발 네트워크
    hardhat: {
    },
    // Sepolia 테스트넷 설정
    sepolia: {
      url: `https://sepolia.infura.io/v3/${INFURA_API_KEY}`,
      accounts: [PRIVATE_KEY],
      chainId: 11155111,
      gasMultiplier: 1.2
    }
  },
  // 컨트랙트 확인을 위한 Etherscan 설정
  etherscan: {
    apiKey: ETHERSCAN_API_KEY
  },
  // 긴 스택 트레이스 표시
  mocha: {
    timeout: 40000
  }
};