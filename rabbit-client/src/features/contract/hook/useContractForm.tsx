import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import useContractMutate from "@/entities/contract/hooks/useContractMutate";
import useCreateContract from "@/entities/contract/hooks/useCreateContract";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";
import { useWeb3 } from "@/shared/lib/web3/context/useWeb3";
import { passType } from "@/shared/type/Types";
import dateFormat from "@/shared/utils/dateFormat";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation } from "react-router";
import { toast } from "sonner";
import { z } from "zod";

const useContractForm = () => {
  const { user } = useAuthUser();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMessage, setDialogMessage] = useState("");
  const [isPassDialogOpen, setIsPassDialogOpen] = useState(false);
  const [isModifyDialogOpen, setIsModifyDialogOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { state } = useLocation();
  console.log("state", state);
  const { cancelContract } = useContractMutate({
    contractId: state?.contractId ?? "",
  });
  const [passState, setPassState] = useState<passType>({
    authResultCode: "FAIL",
    passAuthToken: "",
    txId: "",
    phoneNumber: "",
    name: "",
  });
  const { address } = useGetWallet();
  const { web3 } = useWeb3();
  const { createContractMutation } = useCreateContract();

  const contractSchema = z.object({
    drPhone: z.string().min(1, { message: "전화번호를 입력해주세요" }),
    drName: z.string().min(1, { message: "이름을 입력해주세요" }),
    drWallet: z.string().min(1, { message: "지갑 주소를 입력해주세요" }),
    crEmail: z
      .string()
      .min(1, { message: "이메일을 입력해주세요" })
      .email({ message: "올바른 이메일 형식이 아닙니다" }),
    crName: z.string().min(1, { message: "이름을 입력해주세요" }),
    crWallet: z.string().min(1, { message: "지갑 주소를 입력해주세요" }),
    la: z.number().min(99999, { message: "100,000원 이상" }),
    ir: z
      .number()
      .min(0, { message: "이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "이자율은 0.1% 단위로 입력해주세요" }),
    lt: z
      .number()
      .min(1, { message: "대출 기간은 1개월 이상이어야 합니다" })
      .int({ message: "대출 기간은 정수로 입력해주세요" }),
    repayType: z.enum(["EPIP", "EPP", "BP"]),
    mpDt: z
      .number()
      .min(1, { message: "납입일은 1일 이상이어야 합니다" })
      .max(31, { message: "납입일은 31일을 초과할 수 없습니다" }),
    dir: z
      .number()
      .min(0, { message: "연체이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "연체이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "연체이자율은 0.1% 단위로 입력해주세요" }),
    defCnt: z.number().min(0, { message: "기한이익상실 기준을 입력해주세요" }),
    pnTransFlag: z.boolean(),
    earlypay: z.boolean(),
    earlypayFee: z
      .number()
      .min(0, { message: "중도상환 수수료율은 0% 이상이어야 합니다" })
      .max(20, { message: "중도상환 수수료율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "중도상환 수수료율은 0.1% 단위로 입력해주세요" })
      .optional()
      .refine(
        (val: number | undefined) => {
          if (val === undefined) return true;
          return val >= 0;
        },
        {
          message: "중도상환 수수료율을 입력해주세요",
        },
      ),
    addTerms: z.string().optional(),
    message: z.string().optional(),
    passAuthToken: z.string(),
    txId: z.string(),
    authResultCode: z.string(),
    contractDt: z.date(),
  });

  useEffect(() => {
    if (address) {
      form.setValue("drWallet", address);
    }
  }, [address]);

  useEffect(() => {
    if (passState.authResultCode === "SUCCESS") {
      handlePassComplete();
    }
  }, [passState]);

  const form = useForm<z.infer<typeof contractSchema>>({
    resolver: zodResolver(contractSchema),
    defaultValues: {
      drName: user?.userName ?? "",
      drPhone: state?.drPhone ?? "",
      drWallet: state?.drWallet ?? "",
      crName: state?.crName ?? "",
      crEmail: state?.crEmail ?? "",
      crWallet: state?.crWallet ?? "",
      la: state?.la ?? undefined,
      ir: state?.ir ?? undefined,
      lt: state?.lt ?? undefined,
      repayType: state?.repayType ?? undefined,
      mpDt: state?.mpDt ?? undefined,
      dir: state?.dir ?? undefined,
      defCnt: state?.defCnt ?? 0,
      pnTransFlag: state?.pnTransFlag ?? false,
      earlypay: state?.earlypay ?? false,
      earlypayFee: state?.earlypayFee ?? 0,
      addTerms: state?.addTerms ?? "",
      message: "",
      passAuthToken: "",
      txId: "",
      authResultCode: "",
      contractDt: new Date(),
    },
  });

  const onSubmit = async (data: z.infer<typeof contractSchema>) => {
    setIsLoading(true);
    const account = await web3?.eth.getAccounts();
    if (!account) {
      setIsLoading(false);
      return;
    }

    if (state) {
      try {
        setIsModifyDialogOpen(true);
        await cancelContract();
      } catch (error) {
        if (error instanceof Error) {
          toast.error(error.message);
        }
        setIsLoading(false);
        return;
      }
    }

    console.log("보내기 전 데이터", data);
    try {
      await createContractMutation.mutateAsync({
        ...data,
        earlypayFee: data.earlypayFee ?? 0,
        repayType: data.repayType as "EPIP" | "EPP" | "BP",
        addTerms: data.addTerms ?? null,
        message: data.message ?? null,
        contractDt: dateFormat(String(data.contractDt)),
      });
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePassComplete = () => {
    setIsLoading(true);
    console.log("passState", passState);
    if (passState.authResultCode === "SUCCESS") {
      form.setValue("drPhone", passState.phoneNumber);
      form.setValue("drName", passState.name);
      form.setValue("passAuthToken", passState.passAuthToken);
      form.setValue("txId", passState.txId);
      form.setValue("authResultCode", passState.authResultCode);
      setIsPassDialogOpen(false);
    }
    setIsLoading(false);
  };

  const handleModifyConfirm = async () => {
    setIsLoading(true);
    try {
      await cancelContract();
      setIsModifyDialogOpen(false);
    } catch (error) {
      console.error("Contract cancellation failed:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    form,
    onSubmit,
    passState,
    setPassState,
    handlePassComplete,
    dialogOpen,
    setDialogOpen,
    dialogMessage,
    setDialogMessage,
    isPassDialogOpen,
    setIsPassDialogOpen,
    isModify: state ?? false,
    rejectMessage: state?.rejectMessage ?? "",
    isModifyDialogOpen,
    setIsModifyDialogOpen,
    handleModifyConfirm,
    isLoading,
  };
};

export default useContractForm;
