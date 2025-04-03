export type EventType = "상환" | "양도"; //등등등;

export interface NFTEvent {
  event_type: EventType;
  int_amt: number;
  from: string;
  to: string;
  timestamp: string;
}

export interface NFTEventListResponse {
  event_list: NFTEvent[];
}
