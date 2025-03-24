import { createContext } from "react";

interface AuthContextType {
  isInitialized: boolean;
}
export const AuthContext = createContext<AuthContextType>({
  isInitialized: false,
});
