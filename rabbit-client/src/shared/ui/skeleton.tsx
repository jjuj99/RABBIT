import { cn } from "@/shared/lib/utils";

type SkeletonProps = React.HTMLAttributes<HTMLDivElement>;

const Skeleton = ({ className, ...props }: SkeletonProps) => {
  return (
    <div
      className={cn("animate-pulse rounded-md bg-gray-700/50", className)}
      {...props}
    />
  );
};

export { Skeleton };
