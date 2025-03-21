import { cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";

interface UnitInputProps extends React.ComponentProps<"input"> {
  borderType?: "none" | "white";
  textAlign?: "left" | "right" | "center";
  type: string;
  unit: string;
  className?: string;
  label?: string; // 라벨 텍스트를 위한 prop 추가
  placeholder?: string;
  ariaLabel?: string;
}

const unitInputContainerVariants = cva(
  "flex items-center overflow-hidden rounded-sm bg-gray-600",
  {
    variants: {
      borderType: {
        none: "border-none hover:ring-2 focus-within:ring-[2px] focus-within:ring-positive focus-within:hover:ring-positive",
        white:
          "ring ring-gray-200 hover:ring-2 hover:ring-white focus-within:ring-[2px] focus-within:ring-positive focus-within:hover:ring-positive",
      },
    },
    defaultVariants: {
      borderType: "none",
    },
  },
);

const UnitInput = ({
  id,
  type,
  borderType = "none",
  unit,
  readOnly,
  className,
  textAlign = "right",
  "aria-label": ariaLabel,
}: UnitInputProps) => {
  return (
    <div className={cn(unitInputContainerVariants({ borderType }))}>
      <input
        id={id}
        aria-label={ariaLabel}
        type={type}
        readOnly={readOnly}
        className={cn(
          className,
          "w-full border-none bg-transparent py-1 pl-3 pr-1 text-right outline-none [appearance:textfield] focus:outline-none focus:ring-0 [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none",
          textAlign && `text-${textAlign}`,
        )}
      />
      <span className="whitespace-nowrap pr-3">{unit}</span>
    </div>
  );
};

export default UnitInput;
