export type EventType = "상환" | "양도" | "연체" | "연체 상환";

export interface NFTEvent {
  eventType: EventType;
  intAmt: number | null;
  from: string | null;
  to: string | null;
  timestamp: string;
}

export interface NFTEventListResponse {
  eventList: NFTEvent[];
}
