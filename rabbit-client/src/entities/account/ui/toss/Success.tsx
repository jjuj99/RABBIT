import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router";
import { ConfirmAPI } from "../../api/accountApi";

import { Button } from "@/shared/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/card";

const SuccessPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get("orderId");
  const amount = searchParams.get("amount");
  const paymentKey = searchParams.get("paymentKey");
  console.log("최초렌더링");
  console.log("orderId", orderId);
  console.log("amount", amount);
  console.log("paymentKey", paymentKey);
  console.log("--------------------------------");

  useEffect(() => {
    // 쿼리 파라미터 값이 결제 요청할 때 보낸 데이터와 동일한지 반드시 확인하세요.
    // 클라이언트에서 결제 금액을 조작하는 행위를 방지할 수 있습니다.
    console.log("useEffect 렌더링");
    console.log("orderId", orderId);
    console.log("amount", amount);
    console.log("paymentKey", paymentKey);
    console.log("--------------------------------");
    const requestData = {
      orderId: searchParams.get("orderId"),
      amount: searchParams.get("amount"),
      paymentKey: searchParams.get("paymentKey"),
    };
    console.log(requestData);

    async function confirm() {
      try {
        const response = await ConfirmAPI(requestData);
        console.log(response);
      } catch (error) {
        console.log("error", error);

        if (error instanceof Error) {
          navigate(`account/fail?message=${error?.message}`);
        }
      }

      // 결제 성공 비즈니스 로직을 구현하세요.
      // try {
      //   const amount = Number(searchParams.get("amount"));
      //   await mintRabbitToken(amount);
      //   toast.success(`${amount} RAB가 지갑에 추가되었습니다.`);
      // } catch (error) {
      //   toast.error("토큰 민팅에 실패했습니다.");
      //   console.error("민팅 에러:", error);
      // }
    }
    confirm();
  }, []);

  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center p-8">
      <Card className="w-full max-w-md bg-gradient-to-br from-gray-900 to-gray-800">
        <CardHeader className="flex flex-col items-center gap-6">
          <div className="relative">
            <div className="bg-brand-primary/30 absolute -inset-1 animate-pulse rounded-full blur-md"></div>
            <div className="bg-brand-primary relative rounded-full p-4">
              <img
                src="/icons/Rabbit.svg"
                alt="성공"
                className="h-16 w-16 animate-bounce"
              />
            </div>
          </div>
          <CardTitle className="text-center">
            <h2 className="text-brand-primary mb-2 text-3xl font-bold">
              충전 완료!
            </h2>
            <p className="text-sm text-gray-400">
              RAB 코인이 지갑에 추가되었습니다
            </p>
          </CardTitle>
        </CardHeader>

        <CardContent className="space-y-6">
          <div className="space-y-3 rounded-lg bg-gray-800/50 p-4">
            <div className="flex flex-col gap-2">
              <span className="text-gray-400">충전 금액</span>
              <span className="font-pixel text-brand-primary text-xl font-bold break-all">
                {Number(searchParams.get("amount")).toLocaleString()} RAB
              </span>
            </div>
            <div className="flex flex-col gap-2">
              <span className="text-gray-400">주문 번호</span>
              <span className="font-mono text-sm break-all text-gray-300">
                {searchParams.get("orderId")}
              </span>
            </div>
          </div>

          <Button
            variant="primary"
            className="w-full"
            onClick={() => navigate("/account")}
          >
            내 지갑으로 이동
          </Button>
        </CardContent>
      </Card>
    </div>
  );
};
export default SuccessPage;
