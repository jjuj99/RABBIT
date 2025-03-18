import { cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";

interface UnitInputProps extends React.ComponentProps<"input"> {
  borderType?: "none" | "white";
  textAlign?: "left" | "right" | "center";
  type: string;
  unit: string;
  className?: string;
}

const unitInputContainerVariants = cva(
  "flex w-[100px] items-center overflow-hidden rounded-sm bg-gray-600",
  {
    variants: {
      borderType: {
        none: "border-none hover:ring-2 focus-within:ring-[2px] focus-within:ring-positive",
        white:
          "ring ring-gray-200 hover:ring-2 hover:ring-white focus-within:ring-[2px] focus-within:ring-positive",
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
}: UnitInputProps) => {
  return (
    <div className={cn(unitInputContainerVariants({ borderType }), className)}>
      <input
        type={type}
        className={cn(
          "w-full [appearance:textfield] border-none bg-transparent py-1 pr-1 pl-3 text-right outline-none focus:ring-0 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none",
          textAlign && `text-${textAlign}`,
        )}
      />
      <span className="pr-3">{unit}</span>
    </div>
  );
};

export default UnitInput;
