import { useSuspenseQuery } from "@tanstack/react-query";
import { getContractListAPI } from "../api/ContractApi";
// import useGetWallet from "@/entities/wallet/hooks/useGetWallet";

interface UseGetContractProps {
  type: "sent" | "received";
}
const useGetContractList = ({ type }: UseGetContractProps) => {
  // const { address } = useGetWallet();
  const { data, error } = useSuspenseQuery({
    queryKey: ["contract", type],
    queryFn: () => getContractListAPI(type),
    staleTime: 0, // 데이터를 항상 stale로 취급
    gcTime: 0, // 캐시된 데이터를 바로 제거
  });
  return { data, error };
};
export default useGetContractList;
