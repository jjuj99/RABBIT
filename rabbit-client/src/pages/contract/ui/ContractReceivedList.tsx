import { contractReceivedColumns } from "@/entities/contract/constant/contractReceivedColumns";
import {
  ContractStatus,
  statusConfig,
} from "@/entities/contract/constant/statusConfig";
import { repaymentTypeConfig } from "@/entities/contract/types/repaymentTypeConfig";
import { ContractReceivedListResponse } from "@/entities/contract/types/response";
import ContactStatusBadge from "@/entities/contract/ui/ContactStatusBadge";
import FilterOptions, {
  SelectedFilters,
} from "@/entities/contract/ui/FilterOptions";
import SortOptions, { SortConfig } from "@/entities/contract/ui/SortOption";
import { sortData } from "@/entities/contract/utils/sortData";
import { cn } from "@/shared/lib/utils";
import { Badge } from "@/shared/ui/badge";
import { DataCard } from "@/shared/ui/DataCard";
import { DataTable } from "@/shared/ui/DataTable";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import { wonFormat } from "@/shared/utils/wonFormat";
import { X } from "lucide-react";
import { useMemo, useState } from "react";

// 목데이터 수정
const mockData: ContractReceivedListResponse[] = [
  {
    id: "1",
    creditorName: "홍길동",
    creditorWallet: "0x1234567890abcdef",
    amount: 1000000,
    interestRate: 3.5,
    maturityDate: "2024.06.20",
    monthlyPaymentDate: 25,
    loanTerm: 3,
    repaymentType: "EPIP",
    contractDate: "2024.03.20",
    createdAt: "2024.03.15",
    status: "REQUESTED",
  },
  {
    id: "2",
    creditorName: "김철수",
    creditorWallet: "0x9876543210fedcba",
    amount: 5000000,
    interestRate: 5.0,
    maturityDate: "2024.04.15",
    monthlyPaymentDate: 15,
    loanTerm: 6,
    repaymentType: "EPP",
    contractDate: "2024.03.15",
    createdAt: "2024.03.10",
    status: "COMPLETED",
  },
  {
    id: "3",
    creditorName: "이영희",
    creditorWallet: "0x456789abcdef0123",
    amount: 2500000,
    interestRate: 4.2,
    maturityDate: "2024.05.01",
    monthlyPaymentDate: 10,
    loanTerm: 12,
    repaymentType: "BP",
    contractDate: "2024.04.15",
    createdAt: "2024.04.05",
    status: "MODIFIED",
  },
  {
    id: "4",
    creditorName: "박민수",
    creditorWallet: "0xabcdef0123456789",
    amount: 3000000,
    interestRate: 3.8,
    maturityDate: "2024.03.25",
    monthlyPaymentDate: 20,
    loanTerm: 24,
    repaymentType: "EPIP",
    contractDate: "2024.03.20",
    createdAt: "2024.03.15",
    status: "CANCELED",
  },
];

// 메인 컴포넌트
const ContractReceivedList = () => {
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

  const handleRowClick = (contract: ContractReceivedListResponse) => {
    console.log("Contract clicked:", contract);
    // 필요한 경우 상위 컴포넌트로 이벤트를 전달하거나
    // 페이지 이동 등의 로직을 구현
  };

  const filteredAndSortedData = useMemo(() => {
    let result = [...mockData];

    // 필터 적용
    if (selectedFilters.size > 0) {
      result = result.filter((item) => selectedFilters.has(item.status));
    }

    // 정렬 적용
    return sortData(result, sortConfig);
  }, [sortConfig, selectedFilters]);

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
                    <Badge
                      key={status}
                      className="flex items-center gap-2 bg-gray-800 px-3 py-1 text-white"
                    >
                      <span
                        className={cn(
                          "h-2 w-2 rounded-full",
                          statusConfig[status].dotColor,
                        )}
                      />
                      {statusConfig[status].label}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleFilterToggle(status);
                        }}
                        className="ml-1 hover:text-gray-400"
                      >
                        <X className="h-4 w-4" />
                      </button>
                    </Badge>
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
                  <Badge
                    key={status}
                    className="flex items-center gap-2 bg-gray-800 px-3 py-1 text-white"
                  >
                    <span
                      className={cn(
                        "h-2 w-2 rounded-full",
                        statusConfig[status].dotColor,
                      )}
                    />
                    {statusConfig[status].label}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleFilterToggle(status);
                      }}
                      className="ml-1 hover:text-gray-400"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </Badge>
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
                        {contract.creditorName}
                        <span className="ml-1 text-sm text-gray-400">
                          ({truncateAddress(contract.creditorWallet)})
                        </span>
                      </div>
                      <div className="text-lg font-semibold text-white">
                        {wonFormat(contract.amount)}
                      </div>
                    </div>
                    <ContactStatusBadge status={contract.status} />
                  </div>
                )}
                sections={[
                  {
                    title: "",
                    items: [
                      {
                        label: "이자율",
                        render: (contract) => `${contract.interestRate}%`,
                      },
                      {
                        label: "상환 방식",
                        render: (contract) =>
                          repaymentTypeConfig[contract.repaymentType],
                      },
                      {
                        label: "대출 기간",
                        render: (contract) => `${contract.loanTerm}개월`,
                      },
                      {
                        label: "월 상환일",
                        render: (contract) =>
                          `매월 ${contract.monthlyPaymentDate}일`,
                      },
                      {
                        label: "계약 시행일",
                        render: (contract) => contract.contractDate,
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
