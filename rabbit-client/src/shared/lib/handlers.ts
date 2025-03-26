import { http, HttpResponse } from "msw";

export const handlers = [
  http.get("/api/users", () => {
    return HttpResponse.json([
      { id: 1, name: "사용자1" },
      { id: 2, name: "사용자2" },
    ]);
  }),
];
