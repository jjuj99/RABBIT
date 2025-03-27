import { authHandlers } from "@/entities/auth/mock/handler";
import { handlers as auctionHandlers } from "@/features/auction/mocks/handlers";

export const handlers = [...authHandlers, ...auctionHandlers];
