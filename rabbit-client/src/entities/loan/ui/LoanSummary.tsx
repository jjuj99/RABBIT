import { cn } from "@/shared/lib/utils";
import { Separator } from "@/shared/ui/Separator";

interface LoanSummaryProps {
  title: string;
  mainContent: string;
  subContent: string;
  className?: string;
}

const LoanSummary = ({
  title,
  mainContent,
  subContent,
  className,
}: LoanSummaryProps) => {
  return (
    <div>
      <div>
        <div
          className={cn("h-fit rounded-t-sm bg-gray-900 px-5 py-4", className)}
        >
          <div className="flex flex-col gap-2">
            <h3 className="font-semibold text-white">{title}</h3>
            <div className="flex flex-col">
              <span className="text-brand-primary text-xl font-medium">
                {mainContent}
              </span>
              <span className="text-sm font-light text-gray-200">
                {subContent}
              </span>
            </div>
          </div>
        </div>
        <Separator />
      </div>
    </div>
  );
};

export default LoanSummary;
