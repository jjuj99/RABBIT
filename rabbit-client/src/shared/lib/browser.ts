import { setupWorker } from "msw/browser";
import { handlers as loanHandlers } from "@/entities/loan/mocks/handler";
import { authHandlers } from "@/entities/auth/mock/handler";
import { handlers as auctionHandlers } from "@/features/auction/mocks/handlers";

export const worker = setupWorker(
  ...loanHandlers,
  ...authHandlers,
  ...auctionHandlers,
);
