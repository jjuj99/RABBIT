import { authHandlers } from "@/entities/auth/mock/handler";
import { handlers as auctionHandlers } from "@/features/auction/mocks/handlers";
import { handlers as loanHandlers } from "@/entities/loan/mocks/handler";
import { commonHandler } from "@/widget/common/mock/commonHandler";
import { contractHandler } from "@/entities/contract/mock/handler";
export const handlers = [
  ...commonHandler,
  ...authHandlers,
  ...auctionHandlers,
  ...loanHandlers,
  ...contractHandler,
];
