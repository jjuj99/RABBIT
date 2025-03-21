import UnitInput from "@/entities/common/ui/UnitInput";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/ui/select";
import { Textarea } from "@/shared/ui/textarea";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

const ContractCreate = () => {
  const contractSchema = z.object({
    DR_PHONE: z.string().min(1),
    DR_NAME: z.string().min(1),
    DR_WALLET: z.string().min(1),
    LA: z.number().min(1),
    IR: z.number().min(1),
    LT: z.number().min(1),
    REPAY_TYPE: z.string().min(1),
    DUE_DATE: z.number().min(1),
    DIR: z.number().min(1),
    PN_TRANS: z.boolean().optional(),
    ADDITIONAL_CLAUSE: z.string().optional(),
    MESSAGE: z.string().optional(),
  });
  const { register, handleSubmit } = useForm<z.infer<typeof contractSchema>>({
    resolver: zodResolver(contractSchema),
  });

  const handleSearchUser = () => {
    console.log("search user");
  };

  return (
    <main className="mt-9">
      <form className="flex w-full flex-col items-center gap-4">
        <h2 className="text-3xl">신규 차용증 작성</h2>
        <span className="text-text-disabled text-2xl">
          입력된 내용은 계약서에 반영되면, 법적 효력이 발생합니다.
        </span>
        <div className="flex w-full flex-col gap-3 md:flex-row">
          {/* 채무자 정보 */}
          <div className="flex w-full flex-col gap-3">
            <h3 className="text-2xl font-bold">채무자 정보</h3>
            <div className="flex items-center gap-3">
              <Input id="DR-phone" placeholder="휴대폰 번호를 입력하세요." />
              <Button type="button" variant="gradient">
                인증받기
              </Button>
            </div>
            <div className="flex items-center gap-3">
              <Input readOnly id="DR-name" />
              <Input readOnly id="DR-wallet" />
            </div>
          </div>
          {/* 채권자 정보 */}
          <div className="flex w-full flex-col gap-3">
            <h3 className="text-2xl font-bold">채권자 정보</h3>
            <div className="flex items-center gap-3">
              <Input
                id="CR-email"
                onClick={handleSearchUser}
                readOnly
                placeholder="이메일을 입력하세요."
              />
              <Button
                type="button"
                onClick={handleSearchUser}
                variant="gradient"
              >
                검색
              </Button>
            </div>
            <div className="flex items-center gap-3">
              <Input readOnly onClick={handleSearchUser} id="CR-name" />
              <Input readOnly onClick={handleSearchUser} id="CR-wallet" />
            </div>
          </div>
        </div>
        {/* 1. 기본 정보 */}
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">1. 기본 정보</h3>
          <div className="grid grid-cols-2 items-center gap-3 md:grid-cols-4">
            <UnitInput
              unit="RAB"
              type="number"
              className="col-span-1"
              id="LA"
            />
            <UnitInput unit="%" type="number" className="col-span-1" id="IR" />
            <UnitInput
              unit="개월"
              type="number"
              className="col-span-1"
              id="LT"
            />

            <Select name="REPAY_TYPE">
              <SelectTrigger className="w-full bg-gray-600 text-base">
                <SelectValue placeholder="상환방식" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="EPIP">원리금 균등 상환</SelectItem>
                <SelectItem value="EPP">원금 균등 상환</SelectItem>
                <SelectItem value="BP">만기 일시 상환</SelectItem>
              </SelectContent>
              <UnitInput unit="일" type="number" id="dueDate" />
              <UnitInput unit="%" type="number" id="DIR" />
            </Select>
          </div>
        </div>
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">2. 기한이익상실</h3>
          <span>
            1. 채무자가 아래 사유 중 하나에 해당하는 경우, 채권자는 기한의
            이익을 상실시킬 수 있으며, 채무자는 즉시 대출 원금 및 이자를 전액
            상환하여야한다.
          </span>
          <div className="flex items-center gap-1">
            <span className="indent-6">{"\u2022"} 원금 또는 이자를 </span>
            <UnitInput className="max-w-[80px]" unit="회" type="number" />
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
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">3. 선택항목</h3>
          <span>차용증 양도 가능 여부 (선택사항)</span>
          <div>
            <input type="checkbox" id="PN_TRANS" />
            <label htmlFor="PN_TRANS">
              채권자가 본 차용증을 자유롭게 양도할 수 있음에 동의하며, 등록한
              이메일로 양도 통지를 받는 것에 동의합니다.
            </label>
          </div>
        </div>
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">4. 추가조항(선택)</h3>
          <Textarea
            className="border-border-primary w-full rounded-md border p-2"
            placeholder="추가조항을 입력하세요."
          />
        </div>
        <div className="flex w-full flex-col gap-3">
          <h3 className="text-2xl font-bold">5. 메세지(선택)</h3>
          <Textarea
            className="border-border-primary w-full rounded-md border p-2"
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
