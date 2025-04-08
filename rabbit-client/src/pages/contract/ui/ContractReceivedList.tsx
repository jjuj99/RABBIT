import { contractReceivedColumns } from "@/entities/contract/constant/contractReceivedColumns";
import { ContractStatus } from "@/entities/contract/constant/statusConfig";
import useGetContractList from "@/entities/contract/hooks/useGetContractList";
import { repaymentTypeConfig } from "@/entities/contract/types/repaymentTypeConfig";
import { ContractListResponse } from "@/entities/contract/types/response";
import ContractStatusBadge from "@/entities/contract/ui/ContractStatusBadge";

import FilterOptions, {
  SelectedFilters,
} from "@/entities/contract/ui/FilterOptions";
import SortOptions, { SortConfig } from "@/entities/contract/ui/SortOption";
import { sortData } from "@/entities/contract/utils/sortData";
import { DataCard } from "@/shared/ui/DataCard";
import { DataTable } from "@/shared/ui/DataTable";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import { wonFormat } from "@/shared/utils/wonFormat";
import { useMemo, useState } from "react";
import { useNavigate } from "react-router";

// 메인 컴포넌트
const ContractReceivedList = () => {
  const navigate = useNavigate();
  const { data: contractList } = useGetContractList({
    type: "received",
  });
  console.log(contractList);

  const [sortConfig, setSortConfig] = useState<SortConfig>({
    field: "createdAt",
    order: "desc",
  });
  const [selectedFilters, setSelectedFilters] = useState<SelectedFilters>(
    new Set(),
  );

  const handleFilterToggle = (status: ContractStatus) => {
    const newFilters = new Set(selectedFilters);
    if (newFilters.has(status)) {
      newFilters.delete(status);
    } else {
      newFilters.add(status);
    }
    setSelectedFilters(newFilters);
  };

  const handleRowClick = (contract: ContractListResponse) => {
    navigate(`/contract/received/${contract.id}`);
  };

  const filteredAndSortedData = useMemo(() => {
    if (!contractList.data) return [];
    let result = [...contractList.data.content!];

    // 필터 적용
    if (selectedFilters.size > 0) {
      result = result.filter((item) =>
        selectedFilters.has(item.contractStatus),
      );
    }

    // 정렬 적용
    return sortData(result, sortConfig);
  }, [sortConfig, selectedFilters, contractList]);

  return (
    <main className="min-h-screen bg-gray-900 p-4 md:p-6 lg:p-8">
      <div className="mx-auto max-w-7xl space-y-6">
        <div className="flex flex-col gap-6">
          <div className="space-y-2">
            <h1 className="text-2xl font-bold text-white">받은 차용증 요청</h1>
            <p className="text-gray-400">
              다른 사람으로부터 받은 차용증 요청 목록입니다. 각 요청을 클릭하여
              상세 내역을 확인하고 수락하거나 거절할 수 있습니다.
            </p>
          </div>
          <div className="flex flex-col gap-4 lg:block">
            <div className="flex items-center justify-end gap-4">
              <div className="hidden lg:flex lg:flex-wrap lg:items-center lg:gap-2">
                {selectedFilters.size > 0 &&
                  Array.from(selectedFilters).map((status) => (
                    <ContractStatusBadge
                      key={status}
                      status={status}
                      onDelete={() => handleFilterToggle(status)}
                    />
                  ))}
              </div>
              <FilterOptions
                selectedFilters={selectedFilters}
                onFilterChange={setSelectedFilters}
              />
              <SortOptions
                currentSort={sortConfig}
                onSortChange={setSortConfig}
              />
            </div>
            {/* 모바일에서만 보이는 필터 뱃지 */}
            {selectedFilters.size > 0 && (
              <div className="flex flex-wrap items-center gap-2 lg:hidden">
                {Array.from(selectedFilters).map((status) => (
                  <ContractStatusBadge
                    key={status}
                    status={status}
                    onDelete={() => handleFilterToggle(status)}
                  />
                ))}
              </div>
            )}
          </div>
        </div>

        {/* 데스크톱 뷰 */}
        <div className="hidden lg:block">
          <DataTable
            columns={contractReceivedColumns}
            data={filteredAndSortedData}
            onRowClick={handleRowClick}
          />
        </div>

        {/* 모바일 뷰 */}
        <div className="lg:hidden">
          <div className="grid gap-4">
            {filteredAndSortedData.map((contract) => (
              <DataCard
                key={contract.id}
                item={contract}
                onClick={handleRowClick}
                renderHeader={(contract) => (
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <div className="font-medium text-white">
                        {contract.name}
                        <span className="ml-1 text-sm text-gray-400">
                          ({truncateAddress(contract.walletAddress)})
                        </span>
                      </div>
                      <div className="text-lg font-semibold text-white">
                        {wonFormat(contract.la)}
                      </div>
                    </div>
                    <ContractStatusBadge status={contract.contractStatus} />
                  </div>
                )}
                sections={[
                  {
                    title: "",
                    items: [
                      {
                        label: "이자율",
                        render: (contract) => `${contract.ir}%`,
                      },
                      {
                        label: "상환 방식",
                        render: (contract) =>
                          repaymentTypeConfig[contract.repayType],
                      },
                      {
                        label: "대출 기간",
                        render: (contract) => `${contract.lt}개월`,
                      },
                      {
                        label: "월 상환일",
                        render: (contract) => `매월 ${contract.mpDt}일`,
                      },
                      {
                        label: "계약 시행일",
                        render: (contract) => contract.contractDt,
                      },
                      {
                        label: "요청일",
                        render: (contract) => contract.createdAt,
                      },
                    ],
                  },
                ]}
              />
            ))}
          </div>
        </div>
      </div>
    </main>
  );
};

export default ContractReceivedList;
