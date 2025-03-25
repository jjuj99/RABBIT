import * as React from "react";
import * as CheckboxPrimitive from "@radix-ui/react-checkbox";
import { CheckIcon } from "lucide-react";
import { cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";

const checkboxVariants = cva(
  "peer size-5 shrink-0 rounded-[4px] border-2 shadow-xs transition-shadow outline-none disabled:cursor-not-allowed disabled:opacity-50",
  {
    variants: {
      checkboxType: {
        default:
          "border-white bg-gray-600 dark:bg-input/30 data-[state=checked]:bg-gray-600 data-[state=checked]:text-primary-foreground data-[state=checked]:border-white dark:data-[state=checked]:bg-gray-600 hover:bg-gray-900 data-[state=checked]:hover:bg-gray-900",
        brand:
          "border-brand-primary dark:bg-green-300 data-[state=checked]:bg-brand-primary data-[state=checked]:text-white data-[state=checked]:border-brand-primary dark:data-[state=checked]:bg-brand-primary hover:bg-gray-600 data-[state=checked]:hover:bg-green-600",
      },
    },
    defaultVariants: {
      checkboxType: "default",
    },
  },
);

const checkIconVariants = cva("size-4", {
  variants: {
    checkboxType: {
      default: "text-white",
      brand: "text-gray-900",
    },
  },
  defaultVariants: {
    checkboxType: "default",
  },
});

interface CheckboxProps
  extends React.ComponentProps<typeof CheckboxPrimitive.Root> {
  checkboxType?: "default" | "brand";
}

function Checkbox({ className, checkboxType, ...props }: CheckboxProps) {
  return (
    <CheckboxPrimitive.Root
      data-slot="checkbox"
      className={cn(
        // 컬러 관련 variant 적용
        checkboxVariants({ checkboxType }),
        // 포커스 스타일
        "focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]",
        // aria-invalid 스타일
        "aria-invalid:ring-destructive/20 aria-invalid:border-destructive dark:aria-invalid:ring-destructive/40",
        className,
      )}
      {...props}
    >
      <CheckboxPrimitive.Indicator
        data-slot="checkbox-indicator"
        className="flex items-center justify-center text-current transition-none"
      >
        <CheckIcon className={cn(checkIconVariants({ checkboxType }))} />
      </CheckboxPrimitive.Indicator>
    </CheckboxPrimitive.Root>
  );
}

export { Checkbox };
