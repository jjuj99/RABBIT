import { ProgressBar } from "@/entities/common/ui/ProgressBar";

interface ContractPeriodProps {
  startDate: string;
  endDate: string;
}

export const ContractPeriod = ({ startDate, endDate }: ContractPeriodProps) => {
  return (
    <div className="flex flex-col gap-4 rounded-sm bg-gray-800 p-6">
      <div className="flex items-center justify-between">
        <div className="flex flex-col gap-1">
          <span className="text-sm text-gray-400">계약일</span>
          <span className="text-xl font-bold text-white">{startDate}</span>
        </div>
        <div className="flex flex-col items-end gap-1">
          <span className="text-sm text-gray-400">계약일</span>
          <span className="text-xl font-bold text-white">{endDate}</span>
        </div>
      </div>
      <ProgressBar startDate={startDate} endDate={endDate} />
    </div>
  );
};
