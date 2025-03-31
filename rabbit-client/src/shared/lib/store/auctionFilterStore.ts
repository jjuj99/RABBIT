import { create } from "zustand";

interface AuctionFilterState {
  max_price: string;
  min_price: string;
  max_ir: string;
  min_ir: string;
  max_rate: string;
  repay_type: string[];
  mat_term: string;
  mat_start: string;
  mat_end: string;
  setMaxPrice: (value: string) => void;
  setMinPrice: (value: string) => void;
  setMaxIr: (value: string) => void;
  setMinIr: (value: string) => void;
  setMaxRate: (value: string) => void;
  setRepayType: (value: string[]) => void;
  setMatTerm: (value: string) => void;
  setMatStart: (value: string) => void;
  setMatEnd: (value: string) => void;
}

export const useAuctionFilterStore = create<AuctionFilterState>((set) => ({
  max_price: "",
  min_price: "",
  max_ir: "",
  min_ir: "",
  max_rate: "",
  repay_type: [],
  mat_term: "",
  mat_start: "",
  mat_end: "",
  setMaxPrice: (max_price) => set({ max_price }),
  setMinPrice: (min_price) => set({ min_price }),
  setMaxIr: (max_ir) => set({ max_ir }),
  setMinIr: (min_ir) => set({ min_ir }),
  setMaxRate: (max_rate) => set({ max_rate }),
  setRepayType: (repay_type) => set({ repay_type }),
  setMatTerm: (mat_term) => set({ mat_term }),
  setMatStart: (mat_start) => set({ mat_start }),
  setMatEnd: (mat_end) => set({ mat_end }),
}));
