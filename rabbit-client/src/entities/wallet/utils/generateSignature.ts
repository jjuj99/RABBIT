type SignatureResult = {
  signature: string | null;
  error?: "USER_REJECTED" | "PROVIDER_NOT_FOUND" | "SIGNATURE_FAILED";
};

const generateSignature = async (
  walletAddress: string,
  message: string,
): Promise<SignatureResult> => {
  try {
    if (!window.ethereum) {
      return { signature: null, error: "PROVIDER_NOT_FOUND" };
    }

    const signature = await window.ethereum.request({
      method: "personal_sign",
      params: [message, walletAddress],
    });

    return { signature: signature as string };
  } catch (error) {
    // MetaMask 서명 거부 에러 코드: 4001
    if (error instanceof Error && "code" in error && error.code === 4001) {
      return { signature: null, error: "USER_REJECTED" };
    }

    return { signature: null, error: "SIGNATURE_FAILED" };
  }
};

export default generateSignature;
