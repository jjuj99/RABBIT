import { create } from "zustand";

interface AuctionFilterState {
  minPrice: string;
  maxPrice: string;
  minIR: string;
  maxIR: string;
  paymentTypes: string[];
  maturity: string;
  startDate: string;
  endDate: string;
  setMinPrice: (value: string) => void;
  setMaxPrice: (value: string) => void;
  setMinIR: (value: string) => void;
  setMaxIR: (value: string) => void;
  setPaymentTypes: (value: string[]) => void;
  setMaturity: (value: string) => void;
  setStartDate: (value: string) => void;
  setEndDate: (value: string) => void;
}

export const useAuctionFilterStore = create<AuctionFilterState>((set) => ({
  minPrice: "",
  maxPrice: "",
  minIR: "",
  maxIR: "",
  paymentTypes: [],
  maturity: "",
  startDate: "",
  endDate: "",
  setMinPrice: (minPrice) => set({ minPrice }),
  setMaxPrice: (maxPrice) => set({ maxPrice }),
  setMinIR: (minIR) => set({ minIR }),
  setMaxIR: (maxIR) => set({ maxIR }),
  setPaymentTypes: (paymentTypes) => set({ paymentTypes }),
  setMaturity: (maturity) => set({ maturity }),
  setStartDate: (startDate) => set({ startDate }),
  setEndDate: (endDate) => set({ endDate }),
}));
