import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import useWalletConnection from "@/entities/auth/hooks/useWalletConnection";

const useContractForm = () => {
  const { user } = useAuthUser();
  const { data: walletData } = useWalletConnection();

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
    LA: z
      .number()
      .min(1000000, { message: "대출 금액은 100,000원 이상이어야 합니다" }),
    IR: z
      .number()
      .min(0, { message: "이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "이자율은 0.1% 단위로 입력해주세요" }),
    LT: z
      .number()
      .min(1, { message: "대출 기간은 1개월 이상이어야 합니다" })
      .int({ message: "대출 기간은 정수로 입력해주세요" }),
    REPAY_TYPE: z.string().min(1, { message: "상환 방식을 선택해주세요" }),
    MP_DT: z
      .number()
      .min(1, { message: "납입일은 1일 이상이어야 합니다" })
      .max(31, { message: "납입일은 31일을 초과할 수 없습니다" }),
    DIR: z
      .number()
      .min(0, { message: "연체이자율은 0% 이상이어야 합니다" })
      .max(20, { message: "연체이자율은 20%를 초과할 수 없습니다" })
      .step(0.1, { message: "연체이자율은 0.1% 단위로 입력해주세요" }),
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

  const form = useForm<z.infer<typeof contractSchema>>({
    resolver: zodResolver(contractSchema),
    defaultValues: {
      DR_PHONE: "",
      DR_NAME: user?.nickname,
      DR_WALLET: walletData?.address,
      CR_EMAIL: "",
      CR_NAME: "",
      CR_WALLET: "",
      LA: 0,
      IR: 0,
      LT: 0,
      REPAY_TYPE: "",
      MP_DT: 0,
      DIR: 0,
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
  const handlePhoneCertification = () => {
    console.log("certification");
  };
  const handleSearchUser = () => {
    console.log("search user");
  };
  return {
    form,
    onSubmit,
    handlePhoneCertification,
    handleSearchUser,
  };
};
export default useContractForm;
