import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import { useEffect, useState } from "react";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";

const useContractForm = () => {
  const { user } = useAuthUser();
  const [passUserName, setPassUserName] = useState("");
  const [passPhoneNumber, setPassPhoneNumber] = useState("");
  const [isPassDialogOpen, setIsPassDialogOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMessage, setDialogMessage] = useState("");
  const { address } = useGetWallet();

  const contractSchema = z.object({
    DR_PHONE: z.string().min(1, { message: "전화번호를 입력해주세요" }),
    DR_NAME: z.string().min(1, { message: "이름을 입력해주세요" }),
    DR_WALLET: z.string().min(1, { message: "지갑 주소를 입력해주세요" }),
    CR_EMAIL: z
      .string()
      .min(1, { message: "이메일을 입력해주세요" })
      .email({ message: "올바른 이메일 형식이 아닙니다" }),
    CR_NAME: z.string().min(1, { message: "이름을 입력해주세요" }),
    CR_WALLET: z.string().min(1, { message: "지갑 주소를 입력해주세요" }),
    LA: z.number().min(99999, { message: "100,000원 이상" }).nullable(),
    IR: z
      .number()
      .min(0, { message: "이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "이자율은 0.1% 단위로 입력해주세요" })
      .nullable(),
    LT: z
      .number()
      .min(1, { message: "대출 기간은 1개월 이상이어야 합니다" })
      .int({ message: "대출 기간은 정수로 입력해주세요" })
      .nullable(),
    REPAY_TYPE: z.string().min(1, { message: "상환 방식을 선택해주세요" }),
    MP_DT: z
      .number()
      .min(1, { message: "납입일은 1일 이상이어야 합니다" })
      .max(31, { message: "납입일은 31일을 초과할 수 없습니다" })
      .nullable(),
    DIR: z
      .number()
      .min(0, { message: "연체이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "연체이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "연체이자율은 0.1% 단위로 입력해주세요" })
      .nullable(),
    DEF_CNT: z.number().min(0, { message: "기한이익상실 기준을 입력해주세요" }),
    PN_TRANS: z.boolean().optional(),
    EARLYPAY: z.boolean().optional(),
    EARLYPAY_FEE: z
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
    ADD_TERMS: z.string().optional(),
    MESSAGE: z.string().optional(),
  });

  useEffect(() => {
    if (address) {
      form.setValue("DR_WALLET", address);
    }
  }, [address]);

  const form = useForm<z.infer<typeof contractSchema>>({
    resolver: zodResolver(contractSchema),
    defaultValues: {
      DR_NAME: user?.userName ?? "",
      DR_PHONE: "",
      DR_WALLET: "",
      CR_NAME: "",
      CR_EMAIL: "",
      CR_WALLET: "",
      LA: undefined,
      IR: undefined,
      LT: undefined,
      REPAY_TYPE: "",
      MP_DT: undefined,
      DIR: undefined,
      DEF_CNT: 0,
      PN_TRANS: false,
      EARLYPAY: false,
      EARLYPAY_FEE: 0,
      ADD_TERMS: "",
      MESSAGE: "",
    },
  });

  const onSubmit = (data: z.infer<typeof contractSchema>) => {
    console.log(data);
  };

  const handlePassComplete = (phoneNumber: string, name: string) => {
    const loggedInUserName = form.getValues("DR_NAME");

    if (loggedInUserName && name !== loggedInUserName) {
      alert("인증하신 이름이 회원정보와 일치하지 않습니다.");
      return false;
    }

    form.setValue("DR_PHONE", phoneNumber);
    setIsPassDialogOpen(false);
    return true;
  };

  return {
    form,
    onSubmit,
    passUserName,
    passPhoneNumber,
    isPassDialogOpen,
    setPassUserName,
    setPassPhoneNumber,
    setIsPassDialogOpen,
    handlePassComplete,
    dialogOpen,
    setDialogOpen,
    dialogMessage,
    setDialogMessage,
  };
};

export default useContractForm;
