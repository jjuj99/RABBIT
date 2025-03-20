import * as React from "react";
import * as LabelPrimitive from "@radix-ui/react-label";

import { cn } from "@/shared/lib/utils";

function Label({
  className,
  ...props
}: React.ComponentProps<typeof LabelPrimitive.Root>) {
  return (
    <LabelPrimitive.Root
      data-slot="label"
      className={cn(
        // 기본 스타일
        "mb-2 flex items-center gap-2 text-sm leading-none font-normal text-white select-none",

        // 비활성화 상태 (group-data와 peer 기반)
        "group-data-[disabled=true]:pointer-events-none group-data-[disabled=true]:opacity-50",
        "peer-disabled:cursor-not-allowed peer-disabled:opacity-50",

        // 추가적인 사용자 정의 클래스
        className,
      )}
      {...props}
    />
  );
}

export { Label };
