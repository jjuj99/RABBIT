import UnitInput from "@/entities/common/ui/UnitInput";
import { Button } from "@/shared/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/ui/select";
import { Textarea } from "@/shared/ui/textarea";
import InputForm from "@/entities/contract/InputForm";
import { Label } from "@/shared/ui/label";
import { Checkbox } from "@/shared/ui/checkbox";

const ContractCreate = () => {
  const handlePhoneCertification = () => {
    console.log("certification");
  };
  const handleSearchUser = () => {
    console.log("search user");
  };

  return (
    <main className="mt-9 md:text-xl">
      <form className="flex w-full flex-col items-center gap-9">
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
            <InputForm
              type="tel"
              label="휴대폰 번호"
              id="DR-PHONE"
              placeholder="휴대폰 번호를 입력하세요."
              buttonText="인증받기"
              onClick={handlePhoneCertification}
            />
            <div className="flex w-full items-center gap-3">
              <InputForm
                type="text"
                label="이름"
                id="DR-NAME"
                placeholder="이름을 입력하세요."
                readOnly
              />

              <InputForm
                type="text"
                label="지갑 정보"
                id="DR-WALLET"
                placeholder="지갑 정보를 입력하세요."
                readOnly
              />
            </div>
          </div>
          {/* 채권자 정보 */}
          <div className="flex w-full flex-col gap-3">
            <h3 className="text-2xl font-bold">채권자 정보</h3>
            <InputForm
              label="이메일"
              type="email"
              id="CR-EMAIL"
              placeholder="이메일을 입력하세요."
              onClick={handleSearchUser}
              buttonText="검색"
            />
            <div className="flex w-full items-center gap-3">
              <InputForm
                type="text"
                label="이름"
                id="CR-NAME"
                placeholder="이름을 입력하세요."
                readOnly
              />

              <InputForm
                type="text"
                label="지갑 정보"
                id="CR-WALLET"
                placeholder="지갑 정보를 입력하세요."
                readOnly
              />
            </div>
          </div>
        </div>
        {/* 1. 기본 정보 */}
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">1. 기본 정보</h3>
          <div className="grid grid-cols-2 items-center gap-3 md:grid-cols-4">
            <InputForm label="대출 금액" unit="RAB" type="number" id="LA" />
            <InputForm label="이자율(연)" unit="%" type="number" id="IR" />
            <InputForm label="대출 기간" unit="개월" type="number" id="LT" />
            <div>
              <Label className="text-xl">상환방식</Label>
              <Select name="REPAY_TYPE">
                <SelectTrigger className="w-full bg-gray-600 text-base">
                  <SelectValue placeholder="상환방식" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="EPIP">원리금 균등 상환</SelectItem>
                  <SelectItem value="EPP">원금 균등 상환</SelectItem>
                  <SelectItem value="BP">만기 일시 상환</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <InputForm label="상환일" unit="일" type="number" id="MP_DT" />
            <InputForm
              label="연체 이자율(연)"
              unit="%"
              type="number"
              id="DIR"
            />
          </div>
        </div>
        <div className="flex w-full flex-col gap-3 border-b-2 pb-4">
          <h3 className="text-2xl font-bold">2. 기한이익상실</h3>
          <div className="flex flex-col gap-4 pl-4">
            <span>
              1. 채무자가 아래 사유 중 하나에 해당하는 경우, 채권자는 기한의
              이익을 상실시킬 수 있으며, 채무자는 즉시 대출 원금 및 이자를 전액
              상환하여야한다.
            </span>
            <div className="flex flex-wrap items-center gap-1 pl-4">
              <span>{"\u2022"} 원금 또는 이자를 </span>
              <UnitInput
                wrapperClassName="w-[80px] inline-flex"
                unit="회"
                id="DEF_CNT"
                type="number"
              />
              <span>이상 연체한 경우</span>
            </div>
            <span>
              2. 기한이익 상실 발생 시, 채권자는 서면, 이메일, SMS 등의 방법으로
              통지할 수 있으며, 통지한 날을 기준으로 즉시 효력이 발생한다.
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
              <Checkbox checkboxType="brand" id="PN_TRANS" />
              <label htmlFor="PN_TRANS">
                채권자가 본 차용증을 자유롭게 양도할 수 있음에 동의하며, 등록한
                이메일로 양도 통지를 받는 것에 동의합니다.
              </label>
            </div>
          </div>
          <div className="flex flex-col gap-1 pl-4">
            <span className="text-text-secondary">
              중도 상환 가능 여부 (선택사항)
            </span>
            <div className="border-border-primary flex items-center gap-2 border-b-2 pb-3">
              <Checkbox checkboxType="brand" id="EARLYPAY" />
              <div>
                <label
                  htmlFor="EARLYPAY"
                  className="flex flex-wrap items-center gap-1"
                >
                  <span>
                    채권자는 채무자의 중도 상환에 동의하며, 이에 따른 중도상환
                    이자율
                  </span>
                  <UnitInput
                    wrapperClassName="w-[80px] inline-flex"
                    unit="%"
                    type="number"
                    id="EARLYPAY_FEE"
                  />
                  <span>가 발생할 수 있음을 동의합니다.</span>
                </label>
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">4. 추가조항(선택)</h3>
          <Textarea
            id="ADD_TERMS"
            className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
            placeholder="추가조항을 입력하세요."
          />
        </div>
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">5. 메세지(선택)</h3>
          <Textarea
            id="MESSAGE"
            className="border-border-primary w-full rounded-md border bg-gray-600 p-3 md:text-xl"
            placeholder="채권자에게 전달할 메세지를 입력하세요."
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
            className="h-11 w-[170px] text-xl font-bold text-gray-700"
            variant="primary"
          >
            제출
          </Button>
        </div>
      </form>
    </main>
  );
};

export default ContractCreate;
