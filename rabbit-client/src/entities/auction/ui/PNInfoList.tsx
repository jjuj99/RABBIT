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
      <PNInfo title="최초 등록일" value={formatDateToYMD(data.createdAt)} />
      <PNInfo title="상환 방식" value={data.repayType} />
      <PNInfo title="원금" value={data.la.toLocaleString() + "원"} />
      <PNInfo
        title="총 수취 예상 금액"
        value={data.totalAmount.toLocaleString() + "원"}
      />
      <PNInfo title="만기일" value={formatDateToYMD(data.matDt)} />
      <PNInfo title="이자율" value={data.ir.toString() + "%"} />
      <PNInfo title="연체 이자율" value={data.dir.toString() + "%"} />
      <PNInfo
        title="중도 상환 수수료"
        value={data.earlypayFlag ? data.earlypayFee + "%" : "불가"}
      />
      <PNInfo title="신용점수" value={data.creditScore.toString() + "점"} />
      <PNInfo title="연체" value={data.defCnt.toString() + "회"} />
    </div>
  );
};

export default PNInfoList;
