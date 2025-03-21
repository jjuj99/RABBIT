import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

const useContractForm = () => {
  const contractSchema = z.object({
    DR_PHONE: z.string().min(1),
    DR_NAME: z.string().min(1),
    DR_WALLET: z.string().min(1),
    CR_EMAIL: z.string().min(1).email(),
    CR_NAME: z.string().min(1),
    CR_WALLET: z.string().min(1),
    LA: z.number().min(1000000), // 대출 금액
    IR: z.number().min(0).max(20).step(0.1), // 이자율 - 법정 최고 이자율(20%) 제한
    LT: z.number().min(1), // 대출 기간
    REPAY_TYPE: z.string().min(1), // 상환방식
    MP_DT: z.number().min(1).max(31), // 월 납입일
    DIR: z.number().min(0).max(20).step(0.1), // 연체이자율 - 법정 최고 이자율(20%) 제한
    DEF_CNT: z.number().min(0), // 기한이익상실 기준
    PN_TRANS: z.boolean().optional(), // 차용증 양도 가능 여부
    EARLYPAY: z.boolean().optional(), // 중도 상환 가능 여부
    EARLYPAY_FEE: z.number().min(0).max(20).step(0.1).optional(), // 중도 상환 이자율 - 법정 최고 이자율(20%) 제한
    ADD_TERMS: z.string().optional(), // 추가조항
    MESSAGE: z.string().optional(), // 메세지
  });
  const { register, handleSubmit } = useForm<z.infer<typeof contractSchema>>({
    resolver: zodResolver(contractSchema),
  });
};
export default useContractForm;
