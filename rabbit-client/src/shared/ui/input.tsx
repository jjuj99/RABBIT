import * as React from "react";
import { cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";

interface InputProps extends React.ComponentProps<"input"> {
  borderType?: "none" | "white";
  label?: string;
  placeholder?: string;
}

const inputVariants = cva(
  "file:text-foreground placeholder:text-muted-foreground selection:bg-primary selection:text-primary-foreground",
  {
    variants: {
      borderType: {
        none: "border-none hover:ring-2 focus:ring-[2px] focus:ring-positive",
        white:
          "ring ring-gray-200 hover:ring-2 hover:ring-white focus:ring-[2px] focus:ring-positive",
      },
    },
    defaultVariants: {
      borderType: "none",
    },
  },
); //...

function Input({
  className,
  type,
  borderType,
  label,
  placeholder,
  ...props
}: InputProps) {
  return (
    <>
      {label && (
        <label className="mb-1 ml-1 block text-sm font-medium text-white">
          {label}
        </label>
      )}
      <input
        type={type}
        data-slot="input"
        placeholder={placeholder}
        className={cn(
          "dark:bg-input/30 flex h-[34px] w-full min-w-0 ring-0",
          "rounded-sm bg-gray-600 px-3 text-base shadow-xs transition-[color,box-shadow] outline-none",
          "font-light file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium",
          "disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-base",
          "aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
          inputVariants({ borderType }),
          className,
        )}
        {...props}
      />
    </>
  );
}

export { Input };
