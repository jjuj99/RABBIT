import { create } from "zustand";

interface AuctionFilterState {
  maxPrice: string;
  minPrice: string;
  maxIr: string;
  minIr: string;
  maxRate: string;
  repayType: string[];
  matTerm: string;
  matStart: string;
  matEnd: string;
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
  maxPrice: "",
  minPrice: "",
  maxIr: "",
  minIr: "",
  maxRate: "",
  repayType: [],
  matTerm: "",
  matStart: "",
  matEnd: "",
  setMaxPrice: (maxPrice) => set({ maxPrice }),
  setMinPrice: (minPrice) => set({ minPrice }),
  setMaxIr: (maxIr) => set({ maxIr }),
  setMinIr: (minIr) => set({ minIr }),
  setMaxRate: (maxRate) => set({ maxRate }),
  setRepayType: (repayType) => set({ repayType }),
  setMatTerm: (matTerm) => set({ matTerm }),
  setMatStart: (matStart) => set({ matStart }),
  setMatEnd: (matEnd) => set({ matEnd }),
}));
