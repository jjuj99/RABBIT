import { useAuthUser } from "@/entities/auth/hooks/useAuth";
import useGetWallet from "@/entities/wallet/hooks/useGetWallet";
import { Button } from "@/shared/ui/button";
import {
  loadTossPayments,
  TossPaymentsWidgets,
} from "@tosspayments/tosspayments-sdk";
import { useEffect, useState } from "react";
import { toast } from "sonner";

const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const customerKey = "P8XYv-vzw2JrHwWrEiuUB";

const Checkout = ({
  onClose,
  amount,
}: {
  onClose: () => void;
  amount: number;
}) => {
  const [ready, setReady] = useState(false);
  const [widgets, setWidgets] = useState<TossPaymentsWidgets | null>(null);

  const { user } = useAuthUser();
  const { address } = useGetWallet();
  useEffect(() => {
    async function fetchPaymentWidgets() {
      // ------  결제위젯 초기화 ------
      const tossPayments = await loadTossPayments(clientKey);
      // 회원 결제
      const widgets = tossPayments.widgets({
        customerKey,
      });

      setWidgets(widgets);
    }

    fetchPaymentWidgets();
  }, [clientKey, customerKey]);

  useEffect(() => {
    async function renderPaymentWidgets() {
      if (widgets == null) {
        return;
      }
      // ------ 주문의 결제 금액 설정 ------
      await widgets.setAmount({
        currency: "KRW",
        value: amount,
      });

      await Promise.all([
        // ------  결제 UI 렌더링 ------
        widgets.renderPaymentMethods({
          selector: "#payment-method",
          variantKey: "DEFAULT",
        }),
        // ------  이용약관 UI 렌더링 ------
        widgets.renderAgreement({
          selector: "#agreement",
          variantKey: "AGREEMENT",
        }),
      ]);

      setReady(true);
    }

    renderPaymentWidgets();
  }, [widgets]);

  useEffect(() => {
    if (widgets == null) {
      return;
    }

    widgets.setAmount({
      currency: "KRW",
      value: amount,
    });
  }, [widgets, amount]);

  return (
    <div className="wrapper">
      <div className="box_section">
        {/* 결제 UI */}
        <div id="payment-method" />
        {/* 이용약관 UI */}
        <div id="agreement" />

        {/* 결제하기 버튼 */}
        <div className="mt-4 flex justify-between gap-2">
          <Button
            onClick={onClose}
            type="button"
            variant="outline"
            className="w-[48%]"
          >
            취소하기
          </Button>
          <Button
            type="button"
            variant="primary"
            className="w-[48%]"
            disabled={!ready}
            onClick={async () => {
              try {
                if (amount <= 0) {
                  toast.error("충전 금액을 확인해주세요.");
                  return;
                }

                onClose();
                const timestamp = Date.now().toString();
                const orderId = `${address?.slice(2, 8)}_${timestamp}`;
                await widgets?.requestPayment({
                  orderId,
                  orderName: `${amount}원 결제`,
                  successUrl: window.location.origin + "/account/success",
                  failUrl: window.location.origin + "/account/fail",
                  customerEmail: "test@test.com",
                  customerName: user?.userName,
                  customerMobilePhone: "01012341234",
                });
              } catch (error) {
                console.error(error);
              }
            }}
          >
            결제하기
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
