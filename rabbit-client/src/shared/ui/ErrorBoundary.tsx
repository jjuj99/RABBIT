import { Component, ErrorInfo, ReactNode } from "react";
import { Button } from "@/shared/ui/button";

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
  };

  public static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error,
    };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Uncaught error:", error, errorInfo);
  }

  private handleRetry = () => {
    this.setState({ hasError: false, error: undefined });
    window.location.reload();
  };

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <main className="mt-9 md:text-xl">
          <div className="flex w-full flex-col items-center gap-9 bg-gray-900 px-4 py-9 md:px-11">
            <div className="flex w-full flex-col items-center gap-6">
              <div className="flex flex-col items-center gap-2">
                <svg
                  className="text-brand h-16 w-16"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
                <h2 className="text-2xl font-bold md:text-3xl">
                  오류가 발생했습니다
                </h2>
                <p className="text-text-disabled text-center text-sm md:text-base">
                  {this.state.error?.message ||
                    "예상치 못한 오류가 발생했습니다."}
                </p>
              </div>

              <div className="flex w-full max-w-md flex-col gap-4 text-center text-base text-gray-400">
                <p>
                  죄송합니다. 작업을 처리하는 중에 문제가 발생했습니다.
                  <br />
                  잠시 후 다시 시도해 주세요.
                </p>
              </div>

              <div className="flex justify-center gap-3">
                <Button
                  onClick={() => window.history.back()}
                  className="h-11 flex-1 text-base font-bold text-gray-700 md:max-w-[170px] md:text-lg"
                  variant="secondary"
                >
                  이전 페이지로
                </Button>
                <Button
                  onClick={this.handleRetry}
                  className="h-11 flex-1 text-base font-bold text-gray-700 md:max-w-[170px] md:text-lg"
                  variant="primary"
                >
                  다시 시도
                </Button>
              </div>
            </div>
          </div>
        </main>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
