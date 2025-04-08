import useGetBalance from "@/entities/wallet/hooks/useGetBalance";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";
import RAB from "@/shared/ui/RAB";
import { Circle } from "lucide-react";

const CoinStatus = () => {
  const { address } = useGetWallet();
  const { balance } = useGetBalance();

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 rounded-md bg-gray-900 p-6">
      <div className="flex flex-col gap-2">
        <h3 className="text-xl md:text-2xl">RABBIT 코인 잔액</h3>
        <RAB amount={balance} size="xl" isColored />
      </div>
      <div className="bg-success-dark flex h-fit w-fit items-center gap-2 rounded-[36px] px-4 py-2 text-sm">
        <Circle
          className="text-brand-primary fill-brand-primary"
          width={16}
          height={16}
        />
        <span className="hidden md:block">지갑 연결됨</span>
        <span className="block md:hidden">지갑 연결</span>
        <span className="text-brand-primary hidden max-w-[85px] truncate sm:block">
          {address}
        </span>
      </div>
    </div>
  );
};

export default CoinStatus;
