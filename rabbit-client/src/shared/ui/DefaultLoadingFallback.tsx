import { Loader2 } from "lucide-react";

const DefaultLoadingFallback = () => (
  <div className="flex min-h-[50vh] w-full items-center justify-center">
    <Loader2 className="text-brand h-8 w-8 animate-spin" />
  </div>
);

export default DefaultLoadingFallback;
