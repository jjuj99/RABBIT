import { useSuspenseQuery } from "@tanstack/react-query";
import { getContractListAPI } from "../api/ContractApi";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";

interface UseGetContractProps {
  type: "sent" | "received";
}
const useGetContractList = ({ type }: UseGetContractProps) => {
  const { address } = useGetWallet();
  const { data, error } = useSuspenseQuery({
    queryKey: [address, "contract", type],
    queryFn: () => getContractListAPI(type),
  });
  return { data, error };
};
export default useGetContractList;
