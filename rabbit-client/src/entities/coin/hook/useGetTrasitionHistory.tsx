import { getAccountTransferHistoryAPI } from "@/entities/account/api/accountApi";
import { useQuery } from "@tanstack/react-query";

const useGetTrasitionHistory = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["coinTransferHistory"],
    queryFn: getAccountTransferHistoryAPI,
  });

  return { data, isLoading, error };
};

export default useGetTrasitionHistory;
