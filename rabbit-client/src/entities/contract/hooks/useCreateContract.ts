import { useMutation } from "@tanstack/react-query";
import { createContractAPI, CreateContractRequest } from "../api/ContractApi";
import { useNavigate } from "react-router";
import { toast } from "sonner";

const useCreateContract = () => {
  const navigate = useNavigate();
  const createContractMutation = useMutation({
    mutationFn: (data: CreateContractRequest) => createContractAPI(data),
    onSuccess: (data) => {
      navigate("/contract/new/request-success", {
        state: {
          contractData: data,
        },
      });
    },
    onError: (e) => {
      console.log(e);
      toast.error("계약 요청에 실패했습니다.");
    },
  });

  return { createContractMutation };
};

export default useCreateContract;
