import { ApiResponse } from "@/shared/type/ApiResponse";
import { http, HttpResponse } from "msw";
import { CreateContractRequest } from "../api/ContactApi";
import { CreateContractResponse } from "../types/response";
const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;
export const contractHandler = [
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts`,
    async ({ request }) => {
      const body = (await request.json()) as CreateContractRequest;
      if (!body) {
        const failResponse: ApiResponse<CreateContractResponse> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "Invalid request",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const successResponse: ApiResponse<CreateContractResponse> = {
        status: "SUCCESS",
        data: {
          contractId: "1234567890",
        },
      };
      return HttpResponse.json(successResponse);
    },
  ),
];
