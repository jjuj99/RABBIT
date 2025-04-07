import { AuctionSimilarListResponse } from "@/features/auction/types/response";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { ScrollArea } from "@/shared/ui/scroll-area";
import { formatNumber } from "@/utils/format";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
);

interface AuctionSimilarListProps {
  data: AuctionSimilarListResponse;
}

const AuctionSimilarList = ({ data }: AuctionSimilarListProps) => {
  const { targetAuction, comparisonAuctions } = data;

  // 데이터 정렬 및 퍼센타일 계산
  const sortedAuctions = [...comparisonAuctions, targetAuction].sort(
    (a, b) => b.rr - a.rr,
  );
  const targetIndex = sortedAuctions.findIndex(
    (auction) => auction.auctionId === targetAuction.auctionId,
  );
  const percentile = ((targetIndex + 1) / sortedAuctions.length) * 100;

  return (
    <div className="w-full">
      <div className="flex h-fit w-full flex-col gap-4">
        <h3 className="rounded-sm px-3 py-2 text-lg font-semibold text-white sm:text-xl">
          유사 경매 비교
        </h3>

        {/* 내 정보 표시 */}
        <div className="flex h-fit w-full flex-col gap-4 rounded-lg bg-gray-900 p-4">
          <h3 className="text-lg font-semibold text-white">내 경매 정보</h3>
          <div className="grid grid-cols-2 gap-4">
            <div className="rounded-sm bg-gray-800 p-3">
              <p className="text-gray-400">잔여원금 (RP)</p>
              <p className="text-lg font-bold text-white">
                {formatNumber(targetAuction.rp)}원
              </p>
            </div>
            <div className="rounded-sm bg-gray-800 p-3">
              <p className="text-gray-400">잔여상환일 (RD)</p>
              <p className="text-lg font-bold text-white">
                {targetAuction.rd}일
              </p>
            </div>
            <div className="rounded-sm bg-gray-800 p-3">
              <p className="text-gray-400">수익률 (RR)</p>
              <p className="text-lg font-bold text-white">
                {targetAuction.rr}%
              </p>
            </div>
            <div className="rounded-sm bg-gray-800 p-3">
              <p className="text-gray-400">퍼센타일</p>
              <p className="text-lg font-bold text-white">
                {percentile.toFixed(1)}%
              </p>
            </div>
          </div>
        </div>
        {/* 리스트 */}
        <div className="flex h-fit w-full flex-col gap-4 rounded-lg bg-gray-900 p-4">
          <h3 className="text-lg font-semibold text-white">유사 경매 리스트</h3>
          <div className="rounded-sm bg-gray-800">
            <table className="min-w-full divide-y divide-gray-700">
              <thead className="bg-gray-800">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-400 uppercase">
                    경매 ID
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-400 uppercase">
                    잔여원금
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-400 uppercase">
                    잔여상환일
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-400 uppercase">
                    수익률
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-400 uppercase">
                    퍼센타일
                  </th>
                </tr>
              </thead>
            </table>
            <ScrollArea className="h-[400px]">
              <table className="min-w-full divide-y divide-gray-700">
                <tbody className="divide-y divide-gray-700 bg-gray-800">
                  {sortedAuctions.map((auction) => (
                    <tr
                      key={auction.auctionId}
                      className={
                        auction.auctionId === targetAuction.auctionId
                          ? "bg-blue-900/30"
                          : ""
                      }
                    >
                      <td className="px-6 py-4 whitespace-nowrap text-white">
                        {auction.auctionId}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-white">
                        {formatNumber(auction.rp)}원
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-white">
                        {auction.rd}일
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-white">
                        {auction.rr}%
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-white">
                        {(
                          ((sortedAuctions.findIndex(
                            (a) => a.auctionId === auction.auctionId,
                          ) +
                            1) /
                            sortedAuctions.length) *
                          100
                        ).toFixed(1)}
                        %
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </ScrollArea>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuctionSimilarList;
