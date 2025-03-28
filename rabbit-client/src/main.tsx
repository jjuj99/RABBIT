import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import * as Sentry from "@sentry/react";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router";
import App from "./app/App.tsx";
import "./index.css";
import { AuthProvider } from "./entities/auth/provider/AuthProvider.tsx";
import { Toaster } from "./shared/ui/sonner.tsx";

Sentry.init({
  dsn: "https://2e097efb394a743c3037123fca3b4f36@o4508969411084288.ingest.de.sentry.io/4508969412591696",
});

const queryClient = new QueryClient();

// MSW 활성화 함수
async function enableMocking() {
  // Vite의 환경변수 사용
  if (import.meta.env.MODE === "development") {
    const { worker } = await import("./shared/lib/browser");
    return worker.start();
  }
}

// MSW 초기화 후 앱 렌더링
enableMocking().then(() => {
  createRoot(document.getElementById("root")!).render(
    // <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <App />
          <Toaster />
        </BrowserRouter>
      </AuthProvider>
      {/* 개발 환경에서만 DevTools 표시 */}
      {import.meta.env.DEV && <ReactQueryDevtools />}
    </QueryClientProvider>,
    // </StrictMode>,
  );
});
