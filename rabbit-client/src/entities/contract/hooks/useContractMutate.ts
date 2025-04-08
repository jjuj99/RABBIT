import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  cancelContractAPI,
  completeContractAPI,
  rejectContractAPI,
} from "../api/ContractApi";
import { toast } from "sonner";
import { passType } from "@/shared/type/Types";
import { useNavigate } from "react-router";

interface UseContractMutateProps {
  contractId: string;
}

interface RejectContractParams {
  rejectMessage: string;
}

export const useContractMutate = ({ contractId }: UseContractMutateProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // 계약 승인
  const { mutate: completeContract, isPending: isCompleting } = useMutation({
    mutationFn: (pass: passType) => {
      if (contractId) {
        return completeContractAPI(contractId, pass);
      }
      return Promise.reject(new Error("Contract ID is required"));
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["contract", contractId] });
      navigate(`/contract/received/complete`, { state: data.data });
    },
    onError: (error) => {
      toast.error(error.message);
      queryClient.invalidateQueries({ queryKey: ["contract", contractId] });
      console.error(error);
    },
  });

  // 계약 취소
  const { mutateAsync: cancelContract, isPending: isCanceling } = useMutation({
    mutationFn: () => {
      if (contractId) {
        return cancelContractAPI(contractId);
      }
      return Promise.reject(new Error("Contract ID is required"));
    },
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: ["contract", "sent"],
        }),
        queryClient.invalidateQueries({
          queryKey: ["contract", contractId],
        }),
      ]);
    },
    onError: (error) => {
      toast.error(error.message);
      queryClient.invalidateQueries({
        queryKey: ["contract", "sent"],
      });
      console.error(error);
    },
  });

  // 계약 거절
  const { mutate: rejectContract, isPending: isRejecting } = useMutation({
    mutationFn: ({ rejectMessage }: RejectContractParams) => {
      if (contractId) {
        return rejectContractAPI({
          contractId,
          rejectMessage,
          isCanceled: true,
        });
      }
      return Promise.reject(new Error("Contract ID is required"));
    },
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: ["contract", "received"],
        }),
        queryClient.invalidateQueries({
          queryKey: ["contract", contractId],
        }),
      ]);
    },
    onError: (error) => {
      toast.error(error.message);
      queryClient.invalidateQueries({ queryKey: ["contract", contractId] });
      console.error(error);
    },
  });

  // 계약 수정 요청
  const { mutate: requestModifyContract, isPending: isRequestingModify } =
    useMutation({
      mutationFn: ({ rejectMessage }: RejectContractParams) => {
        if (contractId) {
          return rejectContractAPI({
            contractId,
            rejectMessage,
            isCanceled: false,
          });
        }
        return Promise.reject(new Error("Contract ID is required"));
      },
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["contract", contractId] });
      },
      onError: (error) => {
        toast.error(error.message);
        queryClient.invalidateQueries({ queryKey: ["contract", contractId] });
        console.error(error);
      },
    });

  return {
    completeContract,
    rejectContract,
    requestModifyContract,
    cancelContract,
    isCompleting,
    isCanceling,
    isRejecting,
    isRequestingModify,
  };
};

export default useContractMutate;
