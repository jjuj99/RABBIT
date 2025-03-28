import { cn } from "@/shared/lib/utils";

interface SeparatorProps {
  className?: string;
}

export const Separator = ({ className }: SeparatorProps) => {
  return (
    <div
      className={cn(
        "h-[1px] w-full bg-gradient-to-r from-transparent via-white to-transparent",
        className,
      )}
    />
  );
};
