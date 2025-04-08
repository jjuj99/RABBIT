import { useQuery } from "@tanstack/react-query";
import { getCoinTransferHistoryAPI } from "../api/coinApi";

const useGetTrasitionHistory = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["coinTransferHistory"],
    queryFn: getCoinTransferHistoryAPI,
  });

  return { data, isLoading, error };
};

export default useGetTrasitionHistory;
