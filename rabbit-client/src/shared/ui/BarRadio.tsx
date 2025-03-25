import * as React from "react";
import * as RadioGroupPrimitive from "@radix-ui/react-radio-group";
import { cn } from "@/shared/lib/utils";

function BarRadio({
  className,
  ...props
}: React.ComponentProps<typeof RadioGroupPrimitive.Root>) {
  return (
    <RadioGroupPrimitive.Root
      data-slot="BarRadio"
      className={cn("flex flex-wrap gap-2", className)}
      {...props}
    />
  );
}

function BarRadioItem({
  className,
  children,
  ...props
}: React.ComponentProps<typeof RadioGroupPrimitive.Item> & {
  children?: React.ReactNode;
}) {
  return (
    <RadioGroupPrimitive.Item
      data-slot="BarRadioItem"
      className={cn(
        // 기본UI
        "flex h-8 w-[108px] items-center justify-center rounded-sm border px-4 shadow transition hover:bg-gray-600",
        // 포커스 스타일
        "focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2",
        // 기타 상태 스타일
        "disabled:opacity-50 aria-checked:border-white aria-checked:bg-gray-600 aria-checked:text-white",
        className,
      )}
      {...props}
    >
      {children}
    </RadioGroupPrimitive.Item>
  );
}

export { BarRadio, BarRadioItem };
