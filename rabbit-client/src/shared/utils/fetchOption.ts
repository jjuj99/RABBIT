import { getAccessToken } from "@/entities/auth/utils/authUtils";

type AuthType = "access" | "refresh";

const fetchOption = <T>(
  method: string,
  body?: T,
  authType: AuthType = "access",
) => {
  const accessToken = getAccessToken();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${accessToken ?? ""}`,
  };

  if (authType === "refresh") {
    return {
      method,
      headers,
      credentials: "include" as RequestCredentials,
      body: body ? JSON.stringify(body) : undefined,
    };
  }

  return {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  };
};

export default fetchOption;
