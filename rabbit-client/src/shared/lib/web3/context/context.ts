import { createContext } from "react";
import Web3 from "web3";

interface Web3ContextType {
  web3: Web3 | null;
  isLoading: boolean;
}

export const Web3Context = createContext<Web3ContextType>({
  web3: null,
  isLoading: true,
});
