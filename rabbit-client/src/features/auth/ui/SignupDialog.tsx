import { useState, useEffect } from "react";
import { Button } from "@/shared/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/shared/ui/dialog";
import { Input } from "@/shared/ui/input";
import { Label } from "@/shared/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/ui/select";
import { toast } from "sonner";
import { useForm } from "react-hook-form";

import { zodResolver } from "@hookform/resolvers/zod";
import { SignUpRequest, signupSchema } from "@/entities/auth/types/schema";
import useSignup from "@/entities/auth/hooks/useSignup";
import { BankList } from "@/entities/account/types/response";

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
  const { bankList, send1won, verify1won, checkNickname, signup } = useSignup();

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

  const form = useForm<SignUpRequest>({
    resolver: zodResolver(signupSchema),
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

  const handleCheckNickname = async () => {
    const nickname = form.getValues("nickname");
    if (!nickname) {
      toast.error("닉네임을 입력해주세요");
      return;
    }

    setIsChecking((prev) => ({ ...prev, nickname: true }));
    try {
      await checkNickname(nickname);
      setIsVerified((prev) => ({ ...prev, nickname: true }));
      toast.success("사용 가능한 닉네임입니다");
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("중복된 닉네임입니다");
      }
    } finally {
      setIsChecking((prev) => ({ ...prev, nickname: false }));
    }
  };

  const checkEmail = async () => {
    const email = form.getValues("email");
    if (!email) {
      toast.error("이메일을 입력해주세요");
      return;
    }

    setIsChecking((prev) => ({ ...prev, email: true }));
    try {
      // const response = await checkEmailAPI(email);
      await new Promise((resolve) => setTimeout(resolve, 1000));
      setIsVerified((prev) => ({ ...prev, email: true }));
      toast.success("사용 가능한 이메일입니다");
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("중복된 이메일입니다");
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
      await send1won({
        email: form.getValues("email"),
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

  const onSubmit = async (data: SignUpRequest) => {
    try {
      await signup(data);
      toast.success("회원가입이 완료되었습니다");
      setIsSignupModalOpen(false);
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("회원가입에 실패했습니다");
      }
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

        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
          {/* 이메일 */}
          <div className="space-y-2">
            <Label htmlFor="email">이메일</Label>
            <div className="flex gap-2">
              <Input
                id="email"
                type="email"
                {...form.register("email")}
                placeholder="이메일을 입력해주세요"
                className="flex-1"
                readOnly={isVerified.email}
              />
              <Button
                type="button"
                variant="secondary"
                onClick={checkEmail}
                disabled={
                  isChecking.email ||
                  isVerified.email ||
                  !form.getValues("email")?.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)
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
          </div>

          {/* 실명 */}
          <div className="space-y-2">
            <Label htmlFor="name">실명</Label>
            <Input
              id="name"
              {...form.register("name")}
              placeholder="실명을 입력해주세요"
              readOnly={isVerified.account}
            />
            {form.formState.errors.name && (
              <p className="text-sm text-red-500">
                {form.formState.errors.name.message}
              </p>
            )}
          </div>

          {/* 닉네임 */}
          <div className="space-y-2">
            <Label htmlFor="nickname">닉네임</Label>
            <div className="flex gap-2">
              <Input
                id="nickname"
                {...form.register("nickname")}
                placeholder="사용하실 닉네임을 입력해주세요"
                className="flex-1"
                readOnly={isVerified.nickname}
              />
              <Button
                type="button"
                variant="secondary"
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
          </div>

          {/* 계좌정보 */}
          <div className="space-y-2">
            <Label>계좌정보</Label>
            <div className="space-y-2">
              <Select
                onValueChange={(value) =>
                  form.setValue("bankId", Number(value))
                }
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

              <div className="flex gap-2">
                <Input
                  {...form.register("refundAccount")}
                  placeholder="계좌번호를 입력해주세요"
                  className="flex-1"
                  readOnly={isVerified.account}
                />
                <Button
                  type="button"
                  variant="secondary"
                  onClick={sendVerification}
                  disabled={
                    isChecking.account ||
                    isVerified.account ||
                    !form.getValues("bankId") ||
                    !form.getValues("refundAccount")?.match(/^\d{10,14}$/)
                  }
                >
                  {isChecking.account
                    ? "송금 중..."
                    : isVerified.account
                      ? "인증 완료"
                      : "1원 송금"}
                </Button>
              </div>
            </div>
          </div>

          {/* 메타마스크 지갑 주소 */}
          <div className="space-y-2">
            <Label htmlFor="metamaskWallet">메타마스크 지갑 주소</Label>
            <Input
              id="metamaskWallet"
              {...form.register("metamaskWallet")}
              placeholder="메타마스크 지갑 주소를 입력해주세요"
            />
            {form.formState.errors.metamaskWallet && (
              <p className="text-sm text-red-500">
                {form.formState.errors.metamaskWallet.message}
              </p>
            )}
          </div>

          {/* 인증번호 입력 */}
          {showVerificationCode && !isVerified.account && (
            <div className="space-y-2">
              <Label htmlFor="verificationCode">인증번호</Label>
              <div className="flex gap-2">
                <Input
                  id="verificationCode"
                  {...form.register("verificationCode")}
                  placeholder="인증번호를 입력해주세요"
                  className="flex-1"
                />
                <Button type="button" onClick={verifyAccount}>
                  확인
                </Button>
              </div>
            </div>
          )}

          <Button
            type="submit"
            variant="primary"
            className="w-full"
            disabled={
              !isVerified.nickname || !isVerified.email || !isVerified.account
            }
          >
            회원가입
          </Button>
        </form>
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
