import UnitInput from "@/entities/common/ui/UnitInput";
import { InputForm, SelectRepayType } from "@/entities/contract";
import { useContractForm } from "@/features/contract";
import { Button } from "@/shared/ui/button";
import { Checkbox } from "@/shared/ui/checkbox";
import { Form, FormField } from "@/shared/ui/form";
import { Textarea } from "@/shared/ui/textarea";
import BasicDialog from "@/widget/common/ui/BasicDialog";
import EmailSearchDialog from "@/widget/common/ui/EmailSearchDialog";
import LoadingOverlay from "@/widget/common/ui/LoadingOverray";
import PASSDialog from "@/widget/common/ui/PASSDialog";
import { InfoIcon } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const ContractCreate = () => {
  const {
    form,
    onSubmit,
    dialogOpen,
    setDialogOpen,
    dialogMessage,
    isPassDialogOpen,
    setIsPassDialogOpen,
    setPassState,
    isModify,
    rejectMessage,
    isLoading,
  } = useContractForm();

  const [isSearchUserDialogOpen, setIsSearchUserDialogOpen] = useState(false);

  return (
    <>
      <LoadingOverlay
        isLoading={isLoading}
        content={[
          "계약 정보를 불러오는 중입니다...",
          "최대 2분 소요됩니다...",
          "조금만 기다려주세요...",
          "NFT가 생성되는 중 입니다.",
          "그거 아시나요? 저는 모릅니다.",
          "RABBIT의 뜻은 토끼입니다.",
        ]}
      />
      <main className="mt-9 md:text-xl">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit, (errors) => {
              console.log("유효성 검사 실패", errors);
              toast.error("입력하신 내용을 확인해주세요.");

              // 주요 오류 필드를 찾아 보다 구체적인 오류 메시지 표시
              const errorFields = Object.keys(errors);
              if (errorFields.length > 0) {
                const firstFieldName = errorFields[0] as keyof typeof errors;
                const firstError = errors[firstFieldName];
                if (firstError?.message) {
                  toast.error(firstError.message as string);
                }
              }
            })}
            className="flex w-full flex-col items-center gap-9 bg-gray-900 px-4 py-9 md:px-11"
          >
            <div className="flex w-full flex-col items-center gap-2">
              <h2 className="text-2xl md:text-3xl">
                {isModify ? "수정 차용증 작성" : "신규 차용증 작성"}
              </h2>
              <span className="text-text-disabled text-lg md:text-2xl">
                입력된 내용은 계약서에 반영되면, 법적 효력이 발생합니다.
              </span>
            </div>
            <div className="flex w-full flex-col gap-3 md:flex-row">
              {/* 채무자 정보 */}
              <div className="flex w-full flex-col gap-3">
                <h3 className="text-xl font-bold md:text-2xl">채무자 정보</h3>
                <FormField
                  control={form.control}
                  name="drPhone"
                  render={({ field }) => (
                    <PASSDialog
                      isOpen={isPassDialogOpen}
                      onOpenChange={setIsPassDialogOpen}
                      setPassState={setPassState}
                    >
                      <InputForm
                        type="tel"
                        label="휴대폰 번호"
                        readOnly
                        id="drPhone"
                        placeholder="휴대폰 번호를 입력하세요."
                        buttonText={field.value ? "완료" : "인증받기"}
                        onInputClick={
                          field.value
                            ? undefined
                            : () => setIsPassDialogOpen(true)
                        }
                        onClick={
                          field.value
                            ? undefined
                            : () => setIsPassDialogOpen(true)
                        }
                        disabled={!!field.value}
                        asChild={true}
                        {...field}
                      />
                    </PASSDialog>
                  )}
                />

                <div className="flex w-full items-center gap-3">
                  <FormField
                    control={form.control}
                    name="drName"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="이름"
                        id="drName"
                        placeholder="이름을 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="drWallet"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="지갑 정보"
                        id="drWallet"
                        placeholder="지갑 정보를 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />
                </div>
              </div>
              {/* 채권자 정보 */}
              <div className="flex w-full flex-col gap-3">
                <h3 className="text-xl font-bold md:text-2xl">채권자 정보</h3>
                <FormField
                  control={form.control}
                  name="crEmail"
                  render={({ field }) => (
                    <EmailSearchDialog
                      title="사용자 검색"
                      description="사용자를 검색하세요."
                      open={isModify ? false : isSearchUserDialogOpen}
                      setOpen={setIsSearchUserDialogOpen}
                      onUserSelect={(user) => {
                        form.setValue("crEmail", user.email);
                        form.setValue("crName", user.userName);
                        form.setValue("crWallet", user.walletAddress);
                      }}
                    >
                      <InputForm
                        label="이메일"
                        type="email"
                        id="crEmail"
                        readOnly
                        placeholder="이메일을 입력하세요."
                        onInputClick={
                          isModify
                            ? undefined
                            : () => setIsSearchUserDialogOpen(true)
                        }
                        onClick={
                          isModify
                            ? undefined
                            : () => setIsSearchUserDialogOpen(true)
                        }
                        buttonText="검색"
                        {...field}
                      />
                    </EmailSearchDialog>
                  )}
                />
                <div className="flex w-full items-center gap-3">
                  <FormField
                    control={form.control}
                    name="crName"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="이름"
                        id="crName"
                        placeholder="이름을 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="crWallet"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="지갑 정보"
                        id="crWallet"
                        placeholder="지갑 정보를 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />
                </div>
              </div>
            </div>
            {/* 1. 기본 정보 */}
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">1. 기본 정보</h3>
              <div className="grid grid-cols-2 items-center gap-3 md:grid-cols-4">
                <FormField
                  control={form.control}
                  name="la"
                  render={({ field }) => (
                    <InputForm
                      label="대출 금액"
                      unit="RAB"
                      placeholder="100,000원 이상"
                      type="number"
                      id="la"
                      {...field}
                      onChange={(e) =>
                        field.onChange(
                          e.target.value === ""
                            ? undefined
                            : e.target.valueAsNumber,
                        )
                      }
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="ir"
                  render={({ field }) => (
                    <InputForm
                      label="이자율(연)"
                      unit="%"
                      type="number"
                      placeholder="20% 이하(법정 최고)"
                      id="ir"
                      {...field}
                      onChange={(e) =>
                        field.onChange(
                          e.target.value === ""
                            ? undefined
                            : e.target.valueAsNumber,
                        )
                      }
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="lt"
                  render={({ field }) => (
                    <InputForm
                      min={1}
                      label="대출 기간"
                      unit="개월"
                      type="number"
                      placeholder="대출 기간을 입력하세요."
                      id="lt"
                      {...field}
                      onChange={(e) =>
                        field.onChange(
                          e.target.value === ""
                            ? undefined
                            : e.target.valueAsNumber,
                        )
                      }
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="repayType"
                  render={({ field }) => (
                    <SelectRepayType
                      {...field}
                      onChange={field.onChange}
                      value={field.value}
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="mpDt"
                  render={({ field }) => (
                    <InputForm
                      label="상환일"
                      unit="일"
                      placeholder="상환일을 입력하세요."
                      type="number"
                      id="mpDt"
                      {...field}
                      onChange={(e) =>
                        field.onChange(
                          e.target.value === ""
                            ? undefined
                            : e.target.valueAsNumber,
                        )
                      }
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="dir"
                  render={({ field }) => (
                    <InputForm
                      label="연체 이자율(연)"
                      unit="%"
                      placeholder="20% 이하(법정 최고)"
                      type="number"
                      id="dir"
                      {...field}
                      onChange={(e) =>
                        field.onChange(
                          e.target.value === ""
                            ? undefined
                            : e.target.valueAsNumber,
                        )
                      }
                    />
                  )}
                />
                {/* <FormField
                  control={form.control}
                  name="contractDt"
                  render={({ field }) => (
                    <FormItem className="flex flex-col">
                      <FormLabel className="text-lg md:text-xl">
                        계약 시행일
                      </FormLabel>
                      <Popover>
                        <PopoverTrigger asChild>
                          <FormControl>
                            <Button
                              className="rounded-sm bg-gray-600 font-medium"
                              variant={"outline"}
                            >
                              {field.value
                                ? dateFormat(String(field.value))
                                : "날짜 선택"}
                            </Button>
                          </FormControl>
                        </PopoverTrigger>
                        <PopoverContent className="w-auto p-0" align="start">
                          <Calendar
                            id="contractDt"
                            mode="single"
                            {...field}
                            initialFocus
                            disabled={(date) => date <= new Date()}
                            selected={field.value}
                            onSelect={(e: Date | undefined) => {
                              if (e) {
                                field.onChange(e);
                              }
                            }}
                          />
                        </PopoverContent>
                      </Popover>
                    </FormItem>
                  )}
                /> */}
              </div>
            </div>
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
                  <FormField
                    control={form.control}
                    name="defCnt"
                    render={({ field }) => (
                      <UnitInput
                        wrapperClassName="w-[80px] inline-flex"
                        unit="회"
                        id="defCnt"
                        type="number"
                        {...field}
                        onChange={(e) => {
                          field.onChange(Number(e.target.value));
                        }}
                      />
                    )}
                  />
                  <span>이상 연체한 경우</span>
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
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">3. 선택항목</h3>
              <div className="flex flex-col gap-1 pl-4">
                <span className="text-text-secondary">
                  차용증 양도 가능 여부 (선택사항)
                </span>
                <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
                  <FormField
                    control={form.control}
                    name="pnTransFlag"
                    render={({ field }) => (
                      <Checkbox
                        checkboxType="brand"
                        id="pnTransFlag"
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    )}
                  />
                  <label htmlFor="pnTransFlag">
                    채권자가 본 차용증을 자유롭게 양도할 수 있음에 동의하며,
                    등록한 이메일로 양도 통지를 받는 것에 동의합니다.
                  </label>
                </div>
              </div>
              <div className="flex flex-col gap-1 pl-4">
                <span className="text-text-secondary">
                  중도 상환 가능 여부 (선택사항)
                </span>
                <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
                  <FormField
                    control={form.control}
                    name="earlypay"
                    render={({ field }) => (
                      <Checkbox
                        checkboxType="brand"
                        id="earlypay"
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    )}
                  />
                  <div>
                    <label
                      htmlFor="earlypay"
                      className="flex flex-wrap items-center gap-1"
                    >
                      <span>
                        채권자는 채무자의 중도 상환에 동의하며, 이에 따른
                        중도상환 이자율
                      </span>
                      <FormField
                        control={form.control}
                        name="earlypayFee"
                        render={() => (
                          <UnitInput
                            disabled={!form.watch("earlypay")}
                            wrapperClassName="w-[80px] inline-flex"
                            unit="%"
                            type="number"
                            id="earlypayFee"
                            {...form.register("earlypayFee", {
                              valueAsNumber: true,
                            })}
                          />
                        )}
                      />
                      <span>가 발생할 수 있음을 동의합니다.</span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">
                4. 추가조항(선택)
              </h3>
              <FormField
                control={form.control}
                name="addTerms"
                render={({ field }) => (
                  <Textarea
                    id="addTerms"
                    className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
                    placeholder="추가조항을 입력하세요."
                    {...field}
                  />
                )}
              />
            </div>
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-xl font-bold md:text-2xl">5. 메세지(선택)</h3>
              <FormField
                control={form.control}
                name="message"
                render={({ field }) => (
                  <Textarea
                    id="message"
                    className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
                    placeholder="채권자에게 전달할 메세지를 입력하세요."
                    {...field}
                  />
                )}
              />
            </div>
            {isModify && (
              <div className="flex w-full flex-col gap-3">
                <div className="flex items-center gap-2">
                  <h3 className="text-xl font-bold md:text-2xl">
                    6. 거절 사유
                  </h3>
                  <span className="rounded-full bg-gray-100 px-3 py-1 text-sm text-gray-600">
                    참고용
                  </span>
                </div>
                <div className="rounded-lg bg-gray-600 p-4">
                  <div className="flex items-start gap-2">
                    <InfoIcon className="h-5 w-5 text-gray-400" />
                    <div className="flex-1">
                      <p className="text-sm text-gray-50">
                        이전 거절 사유를 참고하여 새로운 차용증을 작성해주세요.
                      </p>
                      <div className="mt-2 rounded-sm bg-gray-700 p-3 text-base text-gray-50">
                        {rejectMessage
                          ? rejectMessage
                          : "채권자가 거절 사유를 작성하지 않았습니다."}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            <div className="flex w-full justify-center gap-3">
              <Button
                className="h-11 flex-1 text-lg font-bold text-gray-700 md:max-w-[170px] md:text-xl"
                variant="secondary"
              >
                취소
              </Button>
              <Button
                type="submit"
                isLoading={isLoading}
                disabled={isLoading}
                className="h-11 flex-1 text-lg font-bold text-gray-700 md:max-w-[170px] md:text-xl"
                variant="primary"
              >
                제출
              </Button>
            </div>
          </form>
        </Form>
      </main>

      <BasicDialog
        children={<div />}
        open={dialogOpen}
        setOpen={setDialogOpen}
        title="입력 확인"
        description={dialogMessage}
        onConfirm={() => setDialogOpen(false)}
        confirmText="확인"
      />
    </>
  );
};

export default ContractCreate;
