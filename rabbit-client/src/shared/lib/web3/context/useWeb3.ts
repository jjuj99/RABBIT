import { useContext } from "react";
import { Web3Context } from "./context";

export const useWeb3 = () => useContext(Web3Context);
