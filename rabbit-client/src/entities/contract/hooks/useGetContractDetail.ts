import { useSuspenseQuery } from "@tanstack/react-query";
import { getContractDetailAPI } from "../api/ContractApi";

const useGetContractDetail = (contractId: string | undefined) => {
  const { data, error } = useSuspenseQuery({
    queryKey: ["contract", contractId],
    queryFn: () => {
      if (!contractId) {
        throw new Error("Contract ID is required");
      }
      return getContractDetailAPI(contractId);
    },
  });
  return { data, error };
};

export default useGetContractDetail;
