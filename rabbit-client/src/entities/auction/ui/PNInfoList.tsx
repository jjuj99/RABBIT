import { formatDateToYMD } from "@/shared/lib/utils";
import { PNInfoListResponse } from "@/features/auction/types/response";
import PNInfo from "./PNInfo";

export interface PNInfoListProps {
  data?: PNInfoListResponse;
}

const PNInfoList = ({ data }: PNInfoListProps) => {
  if (!data) {
    return null;
  }

  return (
    <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
      <PNInfo title="최초 등록일" value={formatDateToYMD(data.created_at)} />
      <PNInfo title="상환 방식" value={data.repay_type} />
      <PNInfo title="원금" value={data.la.toLocaleString() + "원"} />
      <PNInfo
        title="총 수취 예상 금액"
        value={data.total_amount.toLocaleString() + "원"}
      />
      <PNInfo title="만기일" value={formatDateToYMD(data.mat_dt)} />
      <PNInfo title="이자율" value={data.ir.toString() + "%"} />
      <PNInfo title="연체 이자율" value={data.dir.toString() + "%"} />
      <PNInfo
        title="중도 상환 수수료"
        value={data.earlypay_flag ? data.earlypay_fee + "%" : "불가"}
      />
      <PNInfo title="신용점수" value={data.credit_score.toString() + "점"} />
      <PNInfo title="연체" value={data.def_cnt.toString() + "회"} />
    </div>
  );
};

export default PNInfoList;
