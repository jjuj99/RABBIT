import { Checkbox } from "@/shared/ui/checkbox";
import { useAuctionFilterStore } from "@/shared/lib/store/auctionFilterStore";

const REPAY_TYPE = [
  { id: "EPIP", label: "원리금 균등 상환" },
  { id: "EPP", label: "원금 균등 상환" },
  { id: "BP", label: "만기 일시 상환" },
];

interface REPAY_TYPEProps {
  triggerApi: () => void;
}

const REPAY_TYPESection = ({ triggerApi }: REPAY_TYPEProps) => {
  const paymentTypes = useAuctionFilterStore((state) => state.paymentTypes);
  const setPaymentTypes = useAuctionFilterStore(
    (state) => state.setPaymentTypes,
  );

  const togglePaymentType = (id: string) => {
    if (paymentTypes.includes(id)) {
      setPaymentTypes(paymentTypes.filter((v) => v !== id));
    } else {
      setPaymentTypes([...paymentTypes, id]);
    }
    triggerApi();
  };

  return (
    <div className="flex h-fit w-full flex-col gap-3">
      <h2 className="text-base font-bold">종류</h2>
      <div className="flex flex-col gap-1">
        {REPAY_TYPE.map((item) => {
          const isChecked = paymentTypes.includes(item.id);
          return (
            <div
              key={item.id}
              className="flex w-full cursor-pointer flex-row items-center justify-between rounded-sm px-2 py-1 hover:bg-gray-600"
            >
              <span>{item.label}</span>
              <Checkbox
                checkboxType="default"
                checked={isChecked}
                onCheckedChange={() => togglePaymentType(item.id)}
                onClick={(e) => e.stopPropagation()}
              />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default REPAY_TYPESection;
