import { useMutation } from "@tanstack/react-query";
import { createContractAPI } from "../api/ContractApi";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import { CreateContractRequest } from "../types/request";

const useCreateContract = () => {
  const navigate = useNavigate();
  const createContractMutation = useMutation({
    mutationFn: (data: CreateContractRequest) => createContractAPI(data),
    onSuccess: (response) => {
      console.log("요청 성공 후 데이터", response);
      navigate("/contract/new/request-success", {
        state: {
          contractData: {
            data: response.data,
          },
        },
      });
    },
    onError: (e) => {
      console.log(e);
      toast.error("계약 요청에 실패했습니다.");
    },
  });

  return {
    createContractMutation,
    isLoading: createContractMutation.isPending,
    isError: createContractMutation.isError,
    error: createContractMutation.error,
  };
};

export default useCreateContract;
