import { create } from "zustand";

interface AuctionFilterErrorState {
  errors: {
    price: string;
    ir: string;
    matDt: string;
  };
  setError: (
    key: keyof AuctionFilterErrorState["errors"],
    error: string,
  ) => void;
}

export const useAuctionFilterErrorStore = create<AuctionFilterErrorState>(
  (set) => ({
    errors: {
      price: "",
      ir: "",
      matDt: "",
    },
    setError: (key, error) =>
      set((state) => ({ errors: { ...state.errors, [key]: error } })),
  }),
);
