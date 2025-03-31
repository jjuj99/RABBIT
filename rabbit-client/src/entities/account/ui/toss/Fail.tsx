import { Button } from "@/shared/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/card";
import { useNavigate } from "react-router";
import { useSearchParams } from "react-router";

const FailPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center p-8">
      <Card className="w-full max-w-md bg-gradient-to-br from-gray-900 to-gray-800">
        <CardHeader className="flex flex-col items-center gap-6">
          <div className="relative">
            <div className="bg-destructive/30 absolute -inset-1 animate-pulse rounded-full blur-md"></div>
            <div className="bg-destructive relative rounded-full p-4">
              <svg
                className="h-16 w-16 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </div>
          </div>
          <CardTitle className="text-center">
            <h2 className="text-destructive mb-2 text-3xl font-bold">
              충전 실패
            </h2>
            <p className="text-sm text-gray-400">
              RAB 코인 충전에 실패했습니다
            </p>
          </CardTitle>
        </CardHeader>

        <CardContent className="space-y-6">
          <div className="space-y-3 rounded-lg bg-gray-800/50 p-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-400">에러 코드</span>
              <span className="font-mono text-sm text-gray-300">
                {searchParams.get("code")}
              </span>
            </div>
            <div className="flex flex-col gap-2">
              <span className="text-gray-400">실패 사유</span>
              <span className="text-destructive text-sm break-all">
                {searchParams.get("message")}
              </span>
            </div>
          </div>

          <div className="flex gap-3">
            <Button
              variant="destructive"
              className="w-full"
              onClick={() => navigate("/account")}
            >
              돌아가기
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default FailPage;
