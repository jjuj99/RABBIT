import { Button } from "@/shared/ui/button";
import { wonFormat } from "@/shared/utils/wonFormat";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";

import useContractMutate from "@/entities/contract/hooks/useContractMutate";
import useGetContractDetail from "@/entities/contract/hooks/useGetContractDetail";
import ContractStatusBadge from "@/entities/contract/ui/ContractStatusBadge";
import RejectDialog from "@/entities/contract/ui/RejectDialog";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";
import { cn } from "@/shared/lib/utils";
import { passType } from "@/shared/type/Types";
import { Checkbox } from "@/shared/ui/checkbox";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogTitle,
} from "@/shared/ui/dialog";
import currencyFormat from "@/shared/utils/currencyFormat";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import PASSDialog from "@/widget/common/ui/PASSDialog";
import { DialogTrigger } from "@radix-ui/react-dialog";
import { toast } from "sonner";
import LoadingOverlay from "@/widget/common/ui/LoadingOverray";

const ContractDetail = () => {
  const navigate = useNavigate();
  const { contractId } = useParams(); // URL에서 계약 ID를 가져옴
  const { address } = useGetWallet();
  const { data } = useGetContractDetail(contractId);
  const contract = data?.data;
  const [isRejectDialogOpen, setIsRejectDialogOpen] = useState(false);
  const [isCancelDialogOpen, setIsCancelDialogOpen] = useState(false);
  const [isPassDialogOpen, setIsPassDialogOpen] = useState(false);
  const [passState, setPassState] = useState<passType>({
    authResultCode: "FAIL",
    passAuthToken: "",
    txId: "",
    phoneNumber: "",
    name: "",
  });
  const {
    completeContract,
    rejectContract,
    requestModifyContract,
    cancelContract,
    isCompleting,
    isCanceling,
    isRejecting,
    isRequestingModify,
  } = useContractMutate({
    contractId: contractId ?? "",
  });

  const [isLoading, setIsLoading] = useState(false);

  // useEffect로 전체 로딩 상태 관리
  useEffect(() => {
    setIsLoading(
      isCompleting || isCanceling || isRejecting || isRequestingModify,
    );
  }, [isCompleting, isCanceling, isRejecting, isRequestingModify]);

  const handleApprove = () => {
    try {
      completeContract(passState);
    } catch (error) {
      if (error instanceof Error && error.message) {
        toast.error(`계약 승인 실패 ${error.message}`);
      } else {
        toast.error("계약 승인 실패, 알 수 없는 오류");
      }
    }
  };
  // passState가 변경될 때마다 실행
  useEffect(() => {
    if (passState.authResultCode === "SUCCESS") {
      handleApprove();
    }
  }, [passState]);

  if (!contract) {
    return <div>계약 정보를 찾을 수 없습니다.</div>;
  }

  const handleCancel = async () => {
    try {
      setIsCancelDialogOpen(false);
      await cancelContract();
      console.log("계약 취소 성공");
      toast.success("계약이 취소되었습니다.");
      navigate("/contract/sent");
    } catch (error) {
      if (error instanceof Error && error.message) {
        toast.error(`취소 실패 ${error.message}`);
      } else {
        toast.error("취소 실패, 알 수 없는 오류");
      }
    }
  };

  const handleReject = async (rejectMessage: string) => {
    try {
      setIsRejectDialogOpen(false);
      rejectContract({ rejectMessage });
      toast.success("거절 완료");
      navigate("/contract/received"); // 목록 페이지로 이동
    } catch (error) {
      if (error instanceof Error && error.message) {
        toast.error(`거절 실패 ${error.message}`);
      } else {
        toast.error("거절 실패, 알 수 없는 오류");
      }
    }
  };

  const handleModifyRequest = async (rejectMessage: string) => {
    try {
      setIsRejectDialogOpen(false);
      requestModifyContract({ rejectMessage });
      toast.success("수정 요청 완료");
      navigate("/contract/received"); // 목록 페이지로 이동
    } catch (error) {
      if (error instanceof Error && error.message) {
        toast.error(`수정 요청 실패 ${error.message}`);
      } else {
        toast.error("수정 요청 실패, 알 수 없는 오류");
      }
    }
  };
  console.log("contract", contract);

  const renderActionButtons = () => {
    // 로그인한 사용자가 채무자인 경우
    console.log("contract.drWallet", contract.drWallet);
    console.log("address", address);
    if (contract.drWallet.toLowerCase() === address?.toLowerCase()) {
      if (contract.contractStatus === "REQUESTED") {
        console.log("REQUESTED");
        return (
          <>
            <Button
              onClick={() => navigate(-1)}
              className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
              variant="secondary"
            >
              목록으로
            </Button>
            <Dialog
              open={isCancelDialogOpen}
              onOpenChange={setIsCancelDialogOpen}
            >
              <DialogTrigger asChild>
                <Button
                  className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
                  variant="destructive"
                >
                  취소하기
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogTitle>계약 취소 안내</DialogTitle>
                <DialogDescription>계약을 취소하시겠습니까?</DialogDescription>
                <DialogFooter>
                  <Button
                    variant="secondary"
                    onClick={() => setIsCancelDialogOpen(false)}
                  >
                    닫기
                  </Button>
                  <Button variant="destructive" onClick={handleCancel}>
                    확인
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </>
        );
      }
      if (contract.contractStatus === "MODIFICATION_REQUESTED") {
        return (
          <>
            <Dialog
              open={isCancelDialogOpen}
              onOpenChange={setIsCancelDialogOpen}
            >
              <DialogTrigger asChild>
                <Button
                  className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
                  variant="destructive"
                >
                  취소하기
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogTitle>계약 취소 안내</DialogTitle>
                <DialogDescription>계약을 취소하시겠습니까?</DialogDescription>
                <DialogFooter>
                  <Button
                    variant="secondary"
                    onClick={() => setIsCancelDialogOpen(false)}
                  >
                    닫기
                  </Button>
                  <Button variant="destructive" onClick={handleCancel}>
                    확인
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
            <Button
              className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
              variant="primary"
              onClick={() => {
                navigate("/contract/new", { state: contract });
              }}
            >
              수정하기
            </Button>
          </>
        );
      }
      if (contract.contractStatus === "REJECTED") {
        navigate("/contracts/rejected");
        return null;
      }
      if (contract.contractStatus === "CONTRACTED") {
        navigate("/debt");
        return null;
      }
      if (contract.contractStatus === "CANCELED") {
        return null; // 목록으로 버튼만 표시
      }
    }
    // 로그인한 사용자가 채권자인 경우
    else if (contract.crWallet.toLowerCase() === address?.toLowerCase()) {
      if (contract.contractStatus === "REQUESTED") {
        return (
          <>
            <PASSDialog
              isOpen={isPassDialogOpen}
              onOpenChange={setIsPassDialogOpen}
              setPassState={setPassState}
            >
              {/* 패스로직의 아쉬운 점은 승인 버튼을 눌렀을 때 패스를 열어서 처리한 후 패스 상태가 바뀌면 비즈니스로직을 useEffect로 처리해야 한다는 점이다. */}
              {/* 이러한 접근은 비즈니스 로직이 어디서 처리되는지 알기 어렵다. */}
              <Button
                onClick={() => setIsPassDialogOpen(true)}
                className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
                variant="primary"
              >
                승인하기
              </Button>
            </PASSDialog>
            <Button
              onClick={() => setIsRejectDialogOpen(true)}
              className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
              variant="destructive"
            >
              거절하기
            </Button>
          </>
        );
      }
      if (contract.contractStatus === "MODIFICATION_REQUESTED") {
        return (
          <>
            <Button
              onClick={() => navigate(-1)}
              className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
              variant="secondary"
            >
              목록으로
            </Button>
          </>
        );
      }
      if (contract.contractStatus === "REJECTED") {
        navigate("/contracts/rejected");
        return null;
      }
      if (contract.contractStatus === "CONTRACTED") {
        return (
          <>
            <Button
              onClick={() => navigate("/loan/lent")}
              className="flex-1 text-base font-bold text-gray-700 md:max-w-[150px] md:text-lg"
              variant="secondary"
            >
              나의 채권
            </Button>
          </>
        );
      }
      if (contract.contractStatus === "CANCELED") {
        return null; // 목록으로 버튼만 표시
      }
    }

    return null;
  };

  // 비활성화된 상태인지 확인하는 함수
  const isDisabledState = (status: string) => {
    return status === "CANCELED" || status === "REJECTED";
  };

  // 워터마크 텍스트 결정 함수
  const getWatermarkText = (status: string) => {
    switch (status) {
      case "CANCELED":
        return "취소됨";
      case "REJECTED":
        return "거절됨";
      default:
        return "";
    }
  };

  return (
    <>
      <LoadingOverlay isLoading={isLoading} />
      <main className="mt-9 md:text-xl">
        <div
          className={cn(
            "relative flex w-full flex-col items-center gap-9 bg-gray-900 px-4 py-9 md:px-11",
            isDisabledState(contract.contractStatus) && "opacity-75",
          )}
        >
          {/* 비활성화 상태일 때 보여줄 오버레이 */}
          {isDisabledState(contract.contractStatus) && (
            <>
              {/* 흐림 효과와 반투명한 오버레이 */}
              <div
                className={cn(
                  "pointer-events-none absolute inset-0 backdrop-blur-[1px]",
                  contract.contractStatus === "CANCELED" && "bg-gray-900/30",
                  contract.contractStatus === "REJECTED" && "bg-red-900/20",
                )}
              />

              {/* 워터마크 */}
              <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
                <span
                  className={cn(
                    "rotate-[-45deg] border-8 px-12 py-4 text-6xl font-bold",
                    contract.contractStatus === "CANCELED" &&
                      "border-gray-600 text-gray-600",
                    contract.contractStatus === "REJECTED" &&
                      "border-red-800 text-red-800",
                  )}
                >
                  {getWatermarkText(contract.contractStatus)}
                </span>
              </div>
            </>
          )}

          <div
            className={cn(
              "relative flex w-full flex-col items-center gap-2",
              isDisabledState(contract.contractStatus) && "text-gray-400",
            )}
          >
            <h2 className="text-2xl md:text-3xl">차용증 상세 내역</h2>
            <div className="absolute top-1 right-0">
              <ContractStatusBadge status={contract.contractStatus} size="lg" />
            </div>
          </div>

          <div
            className={cn(
              "flex w-full flex-col gap-3 md:flex-row",
              isDisabledState(contract.contractStatus) && "text-gray-400",
            )}
          >
            {/* 채무자 정보 */}
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">채무자 정보</h3>

              <div className="grid w-full grid-cols-2 items-center gap-3">
                <div className="flex-1">
                  <label className="text-lg text-gray-400 md:text-xl">
                    이름
                  </label>
                  <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                    {contract.drName}
                  </div>
                </div>
                <div className="flex-1">
                  <label className="text-lg text-gray-400 md:text-xl">
                    지갑 정보
                  </label>
                  <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                    {truncateAddress(contract.drWallet)}
                  </div>
                </div>
              </div>
            </div>

            {/* 채권자 정보 */}
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">채권자 정보</h3>

              <div className="grid w-full grid-cols-2 items-center gap-3">
                <div className="flex-1">
                  <label className="text-lg text-gray-400 md:text-xl">
                    이름
                  </label>
                  <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                    {contract.crName}
                  </div>
                </div>
                <div className="flex-1">
                  <label className="text-lg text-gray-400 md:text-xl">
                    지갑 정보
                  </label>
                  <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                    {truncateAddress(contract.crWallet)}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* 1. 기본 정보 */}
          <div className="flex w-full flex-col gap-3">
            <h3 className="text-xl font-bold md:text-2xl">1. 기본 정보</h3>
            <div className="grid grid-cols-2 items-center gap-3 md:grid-cols-4">
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  대출 금액
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {wonFormat(contract.la)}
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  이자율(연)
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {contract.ir}%
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  대출 기간
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {contract.lt}개월
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  상환 방식
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {contract.repayTypeName}
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  상환일
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  매월 {contract.mpDt}일
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  연체 이자율(연)
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {contract.ir}%
                </div>
              </div>
              <div>
                <label className="text-lg text-gray-400 md:text-xl">
                  계약 시행일
                </label>
                <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                  {contract.contractDt}
                </div>
              </div>
            </div>
          </div>

          {/* 2. 기한이익상실 */}
          <div className="flex w-full flex-col gap-3 border-b-2 pb-4">
            <h3 className="text-xl font-bold md:text-2xl">2. 기한이익상실</h3>
            <div className="flex flex-col gap-4 pl-4">
              <span>
                1. 채무자가 아래 사유 중 하나에 해당하는 경우, 채권자는 기한의
                이익을 상실시킬 수 있으며, 채무자는 즉시 대출 원금 및 이자를
                전액 상환하여야한다.
              </span>
              <div className="flex flex-wrap items-center gap-1 pl-4">
                <span>{"\u2022"} 원금 또는 이자를 </span>
                <div className="inline-block rounded-sm bg-gray-600 px-2 py-1 text-base">
                  {contract.defCnt}
                </div>
                <span>회 이상 연체한 경우</span>
              </div>
              <span>
                2. 기한이익 상실 발생 시, 채권자는 서면, 이메일, SMS 등의
                방법으로 통지할 수 있으며, 통지한 날을 기준으로 즉시 효력이
                발생한다.
              </span>
              <span>
                3. 기한이익 상실 이후, 채무자는 즉시 대출 원금 및 이자를
                상환하여야하며, 즉시 대출 원금 및 이자를 상환하지 않은 경우,
                채권자는 채무자에게 법정 최고 이율을 적용할 수 있다.
              </span>
            </div>
          </div>

          {/* 3. 선택항목 */}
          <div className="flex w-full flex-col gap-3">
            <h3 className="text-xl font-bold md:text-2xl">3. 선택항목</h3>
            <div className="flex flex-col gap-1 pl-4">
              <label htmlFor="pnTransFlag" className="text-text-secondary">
                차용증 양도 가능 여부
              </label>
              <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
                <Checkbox id="pnTransFlag" checked={contract.pnTransFlag} />
                <span>
                  채권자가 본 차용증을 자유롭게 양도할 수 있음에 동의하며,
                  등록한 이메일로 양도 통지를 받는 것에 동의합니다.
                </span>
              </div>
            </div>
            <div className="flex flex-col gap-1 pl-4">
              <label htmlFor="earlypay" className="text-text-secondary">
                중도 상환 가능 여부
              </label>
              <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
                <Checkbox id="earlypay" checked={contract.earlypay} />
                <span className="flex flex-wrap items-center gap-1">
                  채권자는 채무자의 중도 상환에 동의하며, 이에 따른 중도상환
                  이자율
                  <div className="inline-block rounded-sm bg-gray-600 px-2 py-1 text-base">
                    {contract.earlypayFee}%
                  </div>
                  가 발생할 수 있음을 동의합니다.
                </span>
              </div>
            </div>
          </div>

          {/* 4. 추가조항 */}
          {contract.addTerms && (
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">4. 추가조항</h3>
              <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                {contract.addTerms}
              </div>
            </div>
          )}

          {/* 5. 메시지 */}
          {contract.message && (
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">5. 메시지</h3>
              <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                {contract.message}
              </div>
            </div>
          )}

          {/* 수정 요청 메시지 */}
          {contract.contractStatus === "MODIFICATION_REQUESTED" && (
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">수정 요청 사항</h3>
              <div className="rounded-sm bg-gray-600 px-3 py-2 text-base">
                {contract.rejectMessage
                  ? contract.rejectMessage
                  : "수정 요청 사항이 없습니다."}
              </div>
            </div>
          )}

          {/* 액션 버튼 */}
          <div className="flex w-full items-center justify-center gap-20">
            <div className="flex flex-1 flex-col gap-3">
              <div className="flex items-center gap-5">
                <span>총 상환 금액 :</span>
                <span className="text-primary">
                  {currencyFormat(contract.matAmt)}RAB
                </span>
              </div>
              <div className="flex items-center gap-5">
                <span>예상 이자 :</span>
                <span className="text-primary">
                  {currencyFormat(contract.matAmt - contract.la)}RAB
                </span>
              </div>
            </div>
            <div className="flex flex-1 justify-end gap-3">
              {renderActionButtons()}
            </div>
          </div>
        </div>
      </main>

      <RejectDialog
        isOpen={isRejectDialogOpen}
        onOpenChange={setIsRejectDialogOpen}
        onReject={handleReject}
        onModify={handleModifyRequest}
      />
    </>
  );
};

export default ContractDetail;
