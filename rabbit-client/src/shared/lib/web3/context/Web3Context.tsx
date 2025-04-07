import React, { useEffect, useState } from "react";
import Web3 from "web3";
import { createWeb3 } from "./provider";
import { Web3Context } from "./context";

export const Web3Provider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [web3, setWeb3] = useState<Web3 | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const web3Instance = createWeb3();
    setWeb3(web3Instance);
    setIsLoading(false);
  }, []);

  return (
    <Web3Context.Provider value={{ web3, isLoading }}>
      {children}
    </Web3Context.Provider>
  );
};
