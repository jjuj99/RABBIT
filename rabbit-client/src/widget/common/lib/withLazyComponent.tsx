import DefaultLoadingFallback from "@/shared/ui/DefaultLoadingFallback";
import ErrorBoundary from "@/shared/ui/ErrorBoundary";
import { ComponentType, lazy, Suspense } from "react";

interface LazyComponentProps {
  fallback?: React.ReactNode;
}

export const withLazyComponent = <P extends object>(
  importFunc: () => Promise<{ default: ComponentType<P> }>,
  options: LazyComponentProps = {},
) => {
  const LazyComponent = lazy(importFunc);

  return (props: P) => (
    <ErrorBoundary>
      <Suspense fallback={options.fallback || <DefaultLoadingFallback />}>
        <LazyComponent {...props} />
      </Suspense>
    </ErrorBoundary>
  );
};
