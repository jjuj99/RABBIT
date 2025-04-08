import { BankList } from "@/entities/account/types/response";
import useSignup from "@/entities/auth/hooks/useSignup";
import { signupSchema } from "@/entities/auth/types/schema";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";
import tokenApprove from "@/entities/wallet/utils/tokenApprove";
import { Button } from "@/shared/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/shared/ui/dialog";
import { Form, FormControl, FormField, FormItem } from "@/shared/ui/form";
import { Input } from "@/shared/ui/input";
import { Label } from "@/shared/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import { Check } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

// 회원가입 확인 모달 컴포넌트
const SignupConfirmDialog = ({
  isOpen,
  onOpenChange,
  onConfirm,
}: {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
}) => {
  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>회원가입 안내</DialogTitle>
          <DialogDescription>
            회원이 아니시네요. 회원가입을 진행하시겠습니까?
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button variant="secondary" onClick={() => onOpenChange(false)}>
            취소
          </Button>
          <Button variant="primary" onClick={onConfirm}>
            회원가입
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

// 기존 SignupDialog 컴포넌트 이름 변경
const SignupFormDialog = ({
  isSignupModalOpen,
  setIsSignupModalOpen,
}: {
  isSignupModalOpen: boolean;
  setIsSignupModalOpen: (isOpen: boolean) => void;
}) => {
  const { bankList, send1won, verify1won, checkNickname, checkEmail, signup } =
    useSignup();
  const { address } = useGetWallet();
  const [isChecking, setIsChecking] = useState({
    nickname: false,
    email: false,
    account: false,
  });

  const [isVerified, setIsVerified] = useState({
    nickname: false,
    email: false,
    account: false,
  });

  const [showVerificationCode, setShowVerificationCode] = useState(false);
  const [banks, setBanks] = useState<BankList[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  // const [contractStatus, setContractStatus] = useState<
  //   "loading" | "ready" | "error"
  // >("loading");
  // const [contractError, setContractError] = useState<string | null>(null);

  const form = useForm<z.infer<typeof signupSchema>>({
    resolver: zodResolver(signupSchema),
    defaultValues: {
      userName: "",
      email: "",
      nickname: "",
      bankId: undefined,
      refundAccount: "",
      walletAddress: "",
      verificationCode: "",
    },
  });

  // 은행 목록 가져오기
  useEffect(() => {
    if (bankList) {
      setBanks(bankList);
    }
  }, [bankList]);

  // 닉네임 변경 감지
  useEffect(() => {
    const subscription = form.watch((value, { name }) => {
      if (name === "nickname" && isVerified.nickname) {
        setIsVerified((prev) => ({ ...prev, nickname: false }));
      }
    });
    return () => subscription.unsubscribe();
  }, [form.watch, isVerified.nickname]);

  // 이메일 변경 감지를 위한 useEffect 추가
  useEffect(() => {
    const subscription = form.watch((value, { name }) => {
      if (name === "email" && isVerified.email) {
        setIsVerified((prev) => ({ ...prev, email: false }));
      }
    });
    return () => subscription.unsubscribe();
  }, [form.watch, isVerified.email]);

  useEffect(() => {
    if (address) {
      form.setValue("walletAddress", address);
    }
  }, [address, form]);

  // 토큰 컨트랙트 초기화 useEffect 제거 - permit 서명 시에만 초기화할 예정

  const handleCheckNickname = async () => {
    const nickname = form.getValues("nickname");
    if (!nickname) {
      toast.error("닉네임을 입력해주세요");
      return;
    }

    setIsChecking((prev) => ({ ...prev, nickname: true }));
    try {
      const response = await checkNickname(nickname);
      if (response.data?.duplicated) {
        toast.error("중복된 닉네임입니다");
      } else {
        setIsVerified((prev) => ({ ...prev, nickname: true }));
        toast.success("사용 가능한 닉네임입니다");
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("닉네임 중복 확인 중 오류가 발생했습니다");
      }
    } finally {
      setIsChecking((prev) => ({ ...prev, nickname: false }));
    }
  };

  const handleCheckEmail = async () => {
    const email = form.getValues("email");
    if (!email) {
      toast.error("이메일을 입력해주세요");
      return;
    }

    setIsChecking((prev) => ({ ...prev, email: true }));
    try {
      const response = await checkEmail(email);
      if (response.data?.duplicated) {
        toast.error("중복된 이메일입니다");
      } else {
        setIsVerified((prev) => ({ ...prev, email: true }));
        toast.success("사용 가능한 이메일입니다");
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("이메일 중복 확인 중 오류가 발생했습니다");
      }
    } finally {
      setIsChecking((prev) => ({ ...prev, email: false }));
    }
  };

  const sendVerification = async () => {
    const { bankId, refundAccount } = form.getValues();
    if (!bankId || !refundAccount) {
      toast.error("은행과 계좌번호를 모두 입력해주세요");
      return;
    }

    setIsChecking((prev) => ({ ...prev, account: true }));
    try {
      const email = form.getValues("email");
      await send1won({
        email,
        accountNumber: refundAccount,
      });
      setShowVerificationCode(true);
      toast.success("1원이 송금되었습니다. 인증번호를 입력해주세요");
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("1원 송금에 실패했습니다");
      }
    } finally {
      setIsChecking((prev) => ({ ...prev, account: false }));
    }
  };

  const verifyAccount = async () => {
    const verificationCode = form.getValues("verificationCode");
    if (!verificationCode) {
      toast.error("인증번호를 입력해주세요");
      return;
    }

    try {
      await verify1won({
        email: form.getValues("email"),
        authCode: verificationCode,
      });
      setIsVerified((prev) => ({ ...prev, account: true }));
      toast.success("계좌 인증이 완료되었습니다");
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("잘못된 인증번호입니다");
      }
    }
  };

  const onSubmit = async (data: z.infer<typeof signupSchema>) => {
    // 폼 검증
    if (!isVerified.nickname || !isVerified.email || !isVerified.account) {
      toast.error("모든 항목의 검증이 필요합니다");
      return;
    }

    setIsSubmitting(true);

    try {
      // 서명 생성
      // const permitData = await createPermitSignature(
      //   tokenAddress,
      //   data.walletAddress,
      //   ownerAddress,
      //   "1000000000000000000",
      //   Date.now() + 1000 * 60 * 5,
      // );
      // console.log("permitData", permitData);

      // //PermitCoinRequest 형식으로 변환
      // const permitCoinRequest: PermitCoinRequest = {
      //   owner: data.walletAddress,
      //   spender: ownerAddress,
      //   value: "1000000000000000000",
      //   deadline: (Date.now() + 1000 * 60 * 5).toString(),
      //   signature: permitData,
      // };
      // console.log("permitCoinRequest", permitCoinRequest);
      // // 토큰 승인 API 호출
      // const permitCoinResponse = await permitCoin(permitCoinRequest);
      // console.log("permitCoinResponse", permitCoinResponse);
      // toast.success("토큰 승인이 완료되었습니다");

      const contractOwnerAddress = import.meta.env
        .VITE_RABBIT_CONTRACT_OWNER_ADDRESS;
      const repaymentAddress = import.meta.env.VITE_RABBIT_REPAYMENT_ADDRESS;
      const auctionAddress = import.meta.env
        .VITE_RABBIT_PROMISSORYNOTE_AUCTION_ADDRESS;
      await tokenApprove(contractOwnerAddress, "1000000000000000000");
      await tokenApprove(repaymentAddress, "1000000000000000000");
      await tokenApprove(auctionAddress, "1000000000000000000");

      // 회원가입 API 호출
      const submitData = {
        ...data,
        bankId: Number(data.bankId),
      };

      const result = await signup(submitData);
      console.log(result);
      toast.success("회원가입이 완료되었습니다");
      setIsSignupModalOpen(false);
    } catch (error) {
      console.error("회원가입 중 오류 발생:", error);

      // 사용자 거부인 경우
      if (error instanceof Error && error.message.includes("User denied")) {
        toast.error("사용자가 서명을 거부했습니다");
      }
      // 기타 오류
      else {
        toast.error(
          `회원가입 중 오류가 발생했습니다: ${error instanceof Error ? error.message : "알 수 없는 오류"}`,
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={isSignupModalOpen} onOpenChange={setIsSignupModalOpen}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold">회원가입</DialogTitle>
          <DialogDescription className="text-gray-500">
            RABBIT은 MetaMask 지갑을 통해 회원가입을 진행합니다.
            <br /> 가입을 위한 추가정보를 입력해주세요.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={async (e) => {
              e.preventDefault();

              // 폼 유효성 검사
              const isValid = await form.trigger();

              if (isValid) {
                try {
                  await onSubmit(form.getValues());
                } catch (error) {
                  console.error("폼 제출 에러:", error);
                }
              }
            }}
            className="space-y-6"
          >
            {/* 이메일 */}
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <Label>이메일</Label>
                  <div className="flex gap-2">
                    <FormControl>
                      <Input
                        type="email"
                        placeholder="이메일을 입력해주세요(.com으로 끝내주세요.)"
                        className="flex-1"
                        {...field}
                      />
                    </FormControl>
                    <Button
                      type="button"
                      variant="gradient"
                      onClick={handleCheckEmail}
                      disabled={
                        isChecking.email ||
                        isVerified.email ||
                        !form.formState.dirtyFields.email ||
                        !!form.formState.errors.email
                      }
                    >
                      {isChecking.email
                        ? "확인 중..."
                        : isVerified.email
                          ? "확인 완료"
                          : "중복확인"}
                    </Button>
                  </div>
                  {form.formState.errors.email && (
                    <p className="text-sm text-red-500">
                      {form.formState.errors.email.message}
                    </p>
                  )}
                </FormItem>
              )}
            />

            {/* 실명 */}
            <FormField
              control={form.control}
              name="userName"
              render={({ field }) => (
                <FormItem>
                  <Label>실명</Label>
                  <FormControl>
                    <Input
                      placeholder="실명을 입력해주세요"
                      readOnly={isVerified.account}
                      {...field}
                    />
                  </FormControl>
                  {form.formState.errors.userName && (
                    <p className="text-sm text-red-500">
                      {form.formState.errors.userName.message}
                    </p>
                  )}
                </FormItem>
              )}
            />

            {/* 닉네임 */}
            <FormField
              control={form.control}
              name="nickname"
              render={({ field }) => (
                <FormItem>
                  <Label>닉네임</Label>
                  <div className="flex gap-2">
                    <FormControl>
                      <Input
                        placeholder="사용하실 닉네임을 입력해주세요"
                        className="flex-1"
                        readOnly={isVerified.nickname}
                        {...field}
                      />
                    </FormControl>
                    <Button
                      type="button"
                      variant="gradient"
                      onClick={handleCheckNickname}
                      disabled={
                        isChecking.nickname ||
                        isVerified.nickname ||
                        form.getValues("nickname")?.length < 2
                      }
                    >
                      {isChecking.nickname
                        ? "확인 중..."
                        : isVerified.nickname
                          ? "확인 완료"
                          : "중복확인"}
                    </Button>
                  </div>
                  {form.formState.errors.nickname && (
                    <p className="text-sm text-red-500">
                      {form.formState.errors.nickname.message}
                    </p>
                  )}
                </FormItem>
              )}
            />

            {/* 계좌정보 */}
            <div className="space-y-2">
              <Label>계좌정보</Label>
              <div className="space-y-2">
                <FormField
                  control={form.control}
                  name="bankId"
                  render={({ field }) => (
                    <FormItem>
                      <Select
                        onValueChange={(value) => {
                          const numValue = Number(value);
                          field.onChange(numValue);
                        }}
                        value={field.value ? field.value.toString() : ""}
                        disabled={isVerified.account}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="은행을 선택해주세요" />
                        </SelectTrigger>
                        <SelectContent>
                          {banks.map((bank) => (
                            <SelectItem
                              key={bank.bankId}
                              value={bank.bankId.toString()}
                            >
                              {bank.bankName}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="refundAccount"
                  render={({ field }) => (
                    <FormItem>
                      <div className="flex gap-2">
                        <FormControl>
                          <Input
                            placeholder="계좌번호를 입력해주세요"
                            readOnly={isVerified.account}
                            {...field}
                          />
                        </FormControl>
                        <Button
                          type="button"
                          variant="gradient"
                          onClick={sendVerification}
                          disabled={
                            isChecking.account ||
                            isVerified.account ||
                            !form.getValues("bankId") ||
                            !!form.formState.errors.refundAccount ||
                            !form.getValues("refundAccount") ||
                            form.getValues("refundAccount").length < 10 ||
                            form.getValues("refundAccount").length > 14
                          }
                        >
                          {isChecking.account
                            ? "송금 중..."
                            : isVerified.account
                              ? "인증 완료"
                              : "1원 송금"}
                        </Button>
                      </div>
                      {form.formState.errors.refundAccount && (
                        <p className="text-sm text-red-500">
                          {form.formState.errors.refundAccount.message}
                        </p>
                      )}
                    </FormItem>
                  )}
                />
              </div>
            </div>

            {/* 인증번호 입력 */}
            {showVerificationCode && !isVerified.account && (
              <FormField
                control={form.control}
                name="verificationCode"
                render={({ field }) => (
                  <FormItem>
                    <Label>인증번호</Label>
                    <div className="flex gap-2">
                      <FormControl>
                        <Input
                          placeholder="인증번호를 입력해주세요"
                          className="flex-1"
                          {...field}
                        />
                      </FormControl>
                      <Button type="button" onClick={verifyAccount}>
                        확인
                      </Button>
                    </div>
                  </FormItem>
                )}
              />
            )}

            {/* 메타마스크 지갑 주소 */}
            <FormField
              control={form.control}
              name="walletAddress"
              render={({ field }) => (
                <FormItem>
                  <Label>메타마스크 지갑 주소</Label>
                  <div className="flex items-center gap-2 rounded-md border border-gray-600 bg-gray-800 px-3 py-2">
                    <div className="flex-1 text-sm text-gray-300">
                      {field.value || "지갑 연결 대기중..."}
                    </div>
                    <div className="flex h-5 w-5 items-center justify-center rounded-full bg-green-500">
                      <Check className="h-3 w-3 text-white" />
                    </div>
                  </div>
                </FormItem>
              )}
            />

            <Button
              type="submit"
              variant="primary"
              className="w-full"
              isLoading={isSubmitting}
              disabled={
                isSubmitting ||
                !isVerified.nickname ||
                !isVerified.email ||
                !isVerified.account
              }
            >
              회원가입
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

// 최상위 합성 컴포넌트
const SignupDialog = ({
  isOpen,
  onOpenChange,
}: {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
}) => {
  const [isSignupConfirmOpen, setIsSignupConfirmOpen] = useState(isOpen);
  const [isSignupFormOpen, setIsSignupFormOpen] = useState(false);

  // isOpen prop이 변경될 때 확인 모달 상태 동기화
  useEffect(() => {
    setIsSignupConfirmOpen(isOpen);
  }, [isOpen]);

  // 확인 모달 상태가 변경될 때 부모 컴포넌트에 알림
  useEffect(() => {
    onOpenChange(isSignupConfirmOpen);
  }, [isSignupConfirmOpen, onOpenChange]);

  const handleConfirm = () => {
    setIsSignupConfirmOpen(false);
    setIsSignupFormOpen(true);
  };

  return (
    <>
      <SignupConfirmDialog
        isOpen={isSignupConfirmOpen}
        onOpenChange={setIsSignupConfirmOpen}
        onConfirm={handleConfirm}
      />
      <SignupFormDialog
        isSignupModalOpen={isSignupFormOpen}
        setIsSignupModalOpen={setIsSignupFormOpen}
      />
    </>
  );
};

export default SignupDialog;
