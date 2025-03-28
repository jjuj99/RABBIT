import { useState, useEffect } from "react";
import getMetaMaskProvider from "../utils/getMetaMaskProvider";

type MetaMaskStatus = {
  isInstalled: boolean;
  isLoading: boolean;
  error?: "NOT_INSTALLED" | "CHECK_FAILED";
};

export const useMetaMaskInstalled = () => {
  const [status, setStatus] = useState<MetaMaskStatus>({
    isInstalled: false,
    isLoading: true,
  });

  useEffect(() => {
    const checkMetaMask = async () => {
      try {
        const provider = await getMetaMaskProvider();
        setStatus({
          isInstalled: provider !== null,
          isLoading: false,
          error: provider === null ? "NOT_INSTALLED" : undefined,
        });
      } catch {
        setStatus({
          isInstalled: false,
          isLoading: false,
          error: "CHECK_FAILED",
        });
      }
    };

    checkMetaMask();
  }, []);

  return status;
};

export default useMetaMaskInstalled;
