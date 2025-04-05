import { useQuery } from "@tanstack/react-query";
import { CommonCodeType } from "../types/request";
import getCommonCodeList from "../api/CommonCodeAPI";

const useGetCommonCode = (codeType: CommonCodeType) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["commonCode", codeType],
    queryFn: () => getCommonCodeList(codeType),
  });

  return { data, isLoading, error };
};

export default useGetCommonCode;
