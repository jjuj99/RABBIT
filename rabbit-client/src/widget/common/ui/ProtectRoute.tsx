import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import { useEffect } from "react";

const ProtectRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuthUser();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      toast.error("로그인이 필요합니다.");
      navigate(-1);
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return null;
  }

  return children;
};

export default ProtectRoute;
