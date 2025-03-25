import PNInfo from "@/entities/auction/PNInfo";
import { formatDateToYMD } from "@/shared/lib/utils";

const PNInfoList = () => {
  const data = {
    auction_id: 1,
    price: 100.22,
    end_date: 123,
    interest_rate: 3.15, //이자율
    loan_amount: 10000000, //원금
    created_at: "2025-03-12T14:30:00", //최초 등록일
    repayment_method: "원리금 균등 상환", //상환 방식
    total_amount: 140000, //만기수취액
    maturity_date: "2025-03-12T14:30:00", //만기일
    late_interest_rate: 1.05, //연체 이자율
    prepayment_fee: 1.01, //중도 상환 수수료
    credit_score: 980, //신용점수
    late_payment_count: 3, //연체 횟수
  };

  return (
    <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
      <PNInfo title="최초 등록일" value={formatDateToYMD(data.created_at)} />
      <PNInfo
        title="총 수취 예상 금액"
        value={data.total_amount.toLocaleString() + "원"}
      />
      <PNInfo title="원금" value={data.loan_amount.toLocaleString() + "원"} />
      <PNInfo title="만기일" value={formatDateToYMD(data.maturity_date)} />
      <PNInfo title="상환 방식" value={data.repayment_method} />
      <PNInfo title="연체" value={data.late_payment_count.toString() + "회"} />
      <PNInfo title="이자율" value={data.interest_rate.toString() + "%"} />
      <PNInfo
        title="연체 이자율"
        value={data.late_interest_rate.toString() + "%"}
      />
      <PNInfo title="신용점수" value={data.credit_score.toString() + "점"} />
    </div>
  );
};

export default PNInfoList;
