import QuickIncreaseButtons from "@/entities/account/ui/QuickIncreaseButtons";
import { UnitInput } from "@/entities/common";
import { Button } from "@/shared/ui/button";
import currencyFormat from "@/shared/utils/currencyFormat";

const CoinChargeForm = ({
  amountState,
}: {
  amountState: [number, React.Dispatch<React.SetStateAction<number>>];
}) => {
  const [amount, setAmount] = amountState;
  return (
    <div className="flex flex-col justify-between gap-6 rounded-md bg-gray-900 p-6">
      <div className="flex items-end gap-2">
        <h3 className="text-xl md:text-2xl">충전하기</h3>
        <span className="text-xs text-gray-400 md:text-sm">(1 RAB ≒ 1 원)</span>
      </div>
      <div className="flex flex-col gap-3">
        <QuickIncreaseButtons setState={setAmount} />
        <div className="flex gap-3">
          <UnitInput
            type="number"
            unit="RAB"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            wrapperClassName="w-[70%]"
            className="w-full text-base font-bold md:text-lg"
            unitColor="brand-primary"
          />
          <Button variant={"primary"} className="w-[30%]">
            충전하기
          </Button>
        </div>
        <div className="flex flex-col justify-between rounded-md bg-gray-800 px-6 py-5 md:flex-row md:items-center">
          <span className="text-sm text-gray-400">충전 후 잔액 : </span>
          <span className="text-brand-primary text-base font-bold md:text-lg">
            {currencyFormat(amount)}
            <span className="font-pixel pl-2">RAB</span>
          </span>
        </div>
      </div>
    </div>
  );
};

export default CoinChargeForm;
