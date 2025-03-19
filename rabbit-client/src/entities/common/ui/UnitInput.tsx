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
}

const unitInputContainerVariants = cva(
  "flex w-[100px] items-center overflow-hidden rounded-sm bg-gray-600",
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
  type,
  borderType = "none",
  unit,
  className,
  textAlign = "right",
  label,
  placeholder,
}: UnitInputProps) => {
  return (
    <div>
      {label && (
        <label className="mb-1 block text-sm font-normal text-white">
          {label}
        </label>
      )}
      <div
        className={cn(unitInputContainerVariants({ borderType }), className)}
      >
        <input
          placeholder={placeholder}
          type={type}
          className={cn(
            "w-full [appearance:textfield] border-none bg-transparent py-1 pr-1 pl-3 text-right outline-none focus:ring-0 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none",
            textAlign && `text-${textAlign}`,
          )}
        />
        <span className="pr-3 whitespace-nowrap">{unit}</span>
      </div>
    </div>
  );
};

export default UnitInput;
