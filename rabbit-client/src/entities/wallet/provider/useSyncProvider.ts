import { useSyncExternalStore } from "react";
import { store } from "@/entities/wallet/store/store";
export const useSyncProviders = () =>
  useSyncExternalStore(store.subscribe, store.value, store.value);
