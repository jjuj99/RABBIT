import { getAccessToken } from "@/entities/auth/utils/authUtils";

type AuthType = "none" | "access" | "refresh";

const fetchOption = <T>(
  method: string,
  body?: T,
  authType: AuthType = "none",
) => {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  const accessToken = getAccessToken();

  switch (authType) {
    case "access":
      if (accessToken) {
        headers["Authorization"] = `Bearer ${accessToken}`;
      }
      break;
    case "refresh":
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
