import { z } from "zod";

export const signupSchema = z.object({
  email: z.string().email("올바른 이메일 형식이 아닙니다"),
  userName: z.string().min(2, "실명은 2자 이상이어야 합니다"),
  nickname: z.string().min(2, "닉네임은 2자 이상이어야 합니다"),
  bankId: z.number().min(1, "은행을 선택해주세요"),
  refundAccount: z
    .string()
    .min(10, "계좌번호는 최소 10자리 이상이어야 합니다")
    .max(14, "계좌번호는 최대 14자리까지 가능합니다"),
  walletAddress: z.string(),
  verificationCode: z.string().optional(),
});

export type SignUpRequest = z.infer<typeof signupSchema>;
