import UnitInput from "@/entities/common/ui/UnitInput";
import { Button } from "@/shared/ui/button";

import { Textarea } from "@/shared/ui/textarea";

import { InputForm, SelectRepayType } from "@/entities/contract";
import { useContractForm } from "@/features/contract";
import { Checkbox } from "@/shared/ui/checkbox";
import { Form, FormField } from "@/shared/ui/form";
import PASSDialog from "@/widget/common/ui/PASSDialog";
import BasicDialog from "@/widget/common/ui/BasicDialog";
import { useState } from "react";
import EmailSearchDialog from "@/widget/common/ui/EmailSearchDialog";

const ContractCreate = () => {
  const {
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
  } = useContractForm();

  const [isSearchUserDialogOpen, setIsSearchUserDialogOpen] = useState(false);

  return (
    <>
      <main className="mt-9 md:text-xl">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit, (errors) => {
              console.log("유효성 검사 실패", errors);
              setDialogOpen(true);
              setDialogMessage("입력하신 내용을 확인해주세요.");
            })}
            className="flex w-full flex-col items-center gap-9 bg-gray-900 px-4 py-9 md:px-11"
          >
            <div className="flex w-full flex-col items-center gap-2">
              <h2 className="text-3xl">신규 차용증 작성</h2>
              <span className="text-text-disabled text-2xl">
                입력된 내용은 계약서에 반영되면, 법적 효력이 발생합니다.
              </span>
            </div>
            <div className="flex w-full flex-col gap-3 md:flex-row">
              {/* 채무자 정보 */}
              <div className="flex w-full flex-col gap-3">
                <h3 className="text-2xl font-bold">채무자 정보</h3>
                <FormField
                  control={form.control}
                  name="DR_PHONE"
                  render={({ field }) => (
                    <PASSDialog
                      isOpen={isPassDialogOpen}
                      onOpenChange={setIsPassDialogOpen}
                      userName={passUserName}
                      phoneNumber={passPhoneNumber}
                      onUserNameChange={setPassUserName}
                      onPhoneNumberChange={setPassPhoneNumber}
                      onComplete={handlePassComplete}
                    >
                      <InputForm
                        type="tel"
                        label="휴대폰 번호"
                        readOnly
                        id="DR-PHONE"
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
                    name="DR_NAME"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="이름"
                        id="DR-NAME"
                        placeholder="이름을 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="DR_WALLET"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="지갑 정보"
                        id="DR-WALLET"
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
                <h3 className="text-2xl font-bold">채권자 정보</h3>
                <FormField
                  control={form.control}
                  name="CR_EMAIL"
                  render={({ field }) => (
                    <EmailSearchDialog
                      title="사용자 검색"
                      description="사용자를 검색하세요."
                      open={isSearchUserDialogOpen}
                      setOpen={setIsSearchUserDialogOpen}
                      onUserSelect={(user) => {
                        form.setValue("CR_EMAIL", user.email);
                        form.setValue("CR_NAME", user.name);
                        form.setValue("CR_WALLET", user.wallet);
                      }}
                    >
                      <InputForm
                        label="이메일"
                        type="email"
                        id="CR-EMAIL"
                        readOnly
                        placeholder="이메일을 입력하세요."
                        onInputClick={() => setIsSearchUserDialogOpen(true)}
                        onClick={() => setIsSearchUserDialogOpen(true)}
                        buttonText="검색"
                        {...field}
                      />
                    </EmailSearchDialog>
                  )}
                />
                <div className="flex w-full items-center gap-3">
                  <FormField
                    control={form.control}
                    name="CR_NAME"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="이름"
                        id="CR-NAME"
                        placeholder="이름을 입력하세요."
                        readOnly
                        {...field}
                      />
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="CR_WALLET"
                    render={({ field }) => (
                      <InputForm
                        type="text"
                        label="지갑 정보"
                        id="CR-WALLET"
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
              <h3 className="text-2xl font-bold">1. 기본 정보</h3>
              <div className="grid grid-cols-2 items-center gap-3 md:grid-cols-4">
                <FormField
                  control={form.control}
                  name="LA"
                  render={({ field }) => (
                    <InputForm
                      label="대출 금액"
                      unit="RAB"
                      placeholder="100,000원 이상"
                      type="number"
                      id="LA"
                      {...field}
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="IR"
                  render={({ field }) => (
                    <InputForm
                      label="이자율(연)"
                      unit="%"
                      type="number"
                      placeholder="20% 이하(법정 최고)"
                      id="IR"
                      {...field}
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="LT"
                  render={({ field }) => (
                    <InputForm
                      min={1}
                      label="대출 기간"
                      unit="개월"
                      type="number"
                      placeholder="대출 기간을 입력하세요."
                      id="LT"
                      {...field}
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="REPAY_TYPE"
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
                  name="MP_DT"
                  render={({ field }) => (
                    <InputForm
                      label="상환일"
                      unit="일"
                      placeholder="상환일을 입력하세요."
                      type="number"
                      id="MP_DT"
                      {...field}
                    />
                  )}
                />
                <FormField
                  control={form.control}
                  name="DIR"
                  render={({ field }) => (
                    <InputForm
                      label="연체 이자율(연)"
                      unit="%"
                      placeholder="20% 이하(법정 최고)"
                      type="number"
                      id="DIR"
                      {...field}
                    />
                  )}
                />
              </div>
            </div>
            <div className="flex w-full flex-col gap-3 border-b-2 pb-4">
              <h3 className="text-2xl font-bold">2. 기한이익상실</h3>
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
                    name="DEF_CNT"
                    render={({ field }) => (
                      <UnitInput
                        wrapperClassName="w-[80px] inline-flex"
                        unit="회"
                        id="DEF_CNT"
                        type="number"
                        {...field}
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
              <h3 className="text-2xl font-bold">3. 선택항목</h3>
              <div className="flex flex-col gap-1 pl-4">
                <span className="text-text-secondary">
                  차용증 양도 가능 여부 (선택사항)
                </span>
                <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
                  <FormField
                    control={form.control}
                    name="PN_TRANS"
                    render={({ field }) => (
                      <Checkbox
                        checkboxType="brand"
                        id="PN_TRANS"
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    )}
                  />
                  <label htmlFor="PN_TRANS">
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
                    name="EARLYPAY"
                    render={({ field }) => (
                      <Checkbox
                        checkboxType="brand"
                        id="EARLYPAY"
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    )}
                  />
                  <div>
                    <label
                      htmlFor="EARLYPAY"
                      className="flex flex-wrap items-center gap-1"
                    >
                      <span>
                        채권자는 채무자의 중도 상환에 동의하며, 이에 따른
                        중도상환 이자율
                      </span>
                      <FormField
                        control={form.control}
                        name="EARLYPAY_FEE"
                        render={({ field }) => (
                          <UnitInput
                            disabled={!form.watch("EARLYPAY")}
                            {...form.register("EARLYPAY_FEE", {
                              valueAsNumber: true,
                            })}
                            wrapperClassName="w-[80px] inline-flex"
                            unit="%"
                            type="number"
                            id="EARLYPAY_FEE"
                            {...field}
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
              <h3 className="text-2xl font-bold">4. 추가조항(선택)</h3>
              <FormField
                control={form.control}
                name="ADD_TERMS"
                render={({ field }) => (
                  <Textarea
                    id="ADD_TERMS"
                    className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
                    placeholder="추가조항을 입력하세요."
                    {...field}
                  />
                )}
              />
            </div>
            <div className="flex w-full flex-col gap-3">
              <h3 className="text-2xl font-bold">5. 메세지(선택)</h3>
              <FormField
                control={form.control}
                name="MESSAGE"
                render={({ field }) => (
                  <Textarea
                    id="MESSAGE"
                    className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
                    placeholder="채권자에게 전달할 메세지를 입력하세요."
                    {...field}
                  />
                )}
              />
            </div>
            <div className="flex w-full justify-center gap-3">
              <Button
                className="h-11 w-[170px] text-xl font-bold text-gray-700"
                variant="secondary"
              >
                취소
              </Button>
              <Button
                type="submit"
                className="h-11 w-[170px] text-xl font-bold text-gray-700"
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
