import { sentryVitePlugin } from "@sentry/vite-plugin";
import path from "path";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    sentryVitePlugin({
      org: "clapsheep",
      project: "javascript-react",
    }),
  ],

  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },

  server: {
    proxy: {
      "/toss-sdk": {
        target: "https://js.tosspayments.com",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/toss-sdk/, ""),
      },
    },
    // Service Worker가 작동할 수 있도록 설정
    // headers: {
    //   // "Cross-Origin-Opener-Policy": "same-origin",
    //   "Cross-Origin-Embedder-Policy": "credentialless",
    // },
  },

  build: {
    sourcemap: true,
  },
});
