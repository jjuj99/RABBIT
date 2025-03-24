import { cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";
import { Input } from "@/shared/ui/input";

interface UnitInputProps extends React.ComponentProps<"input"> {
  borderType?: "none" | "white";
  textAlign?: "left" | "right" | "center";
  type: string;
  unit: string;
  wrapperClassName?: string;
  className?: string;
  label?: string; // 라벨 텍스트를 위한 prop 추가
  placeholder?: string;
  ariaLabel?: string;
  disabled?: boolean;
}

const unitInputContainerVariants = cva(
  "flex items-center w-full overflow-hidden rounded-sm bg-gray-600",
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
  wrapperClassName,
  type,
  borderType = "none",
  unit,
  readOnly,
  className,
  textAlign = "right",
  "aria-label": ariaLabel,
  disabled,
  ...props
}: UnitInputProps) => {
  return (
    <div
      className={cn(
        unitInputContainerVariants({ borderType }),
        wrapperClassName,
      )}
    >
      <Input
        id={id}
        aria-label={ariaLabel}
        type={type}
        disabled={disabled}
        readOnly={readOnly}
        className={cn(
          className,
          "w-full [appearance:textfield] border-none bg-transparent py-1 pr-1 pl-3 text-right outline-none focus:ring-0 focus:outline-none [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none",
          textAlign && `text-${textAlign}`,
        )}
        {...props}
      />
      <span
        className={cn(
          "pr-3 whitespace-nowrap",
          disabled && "text-text-disabled",
        )}
      >
        {unit}
      </span>
    </div>
  );
};

export default UnitInput;
