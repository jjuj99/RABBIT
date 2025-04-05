import { ColumnDef } from "@/shared/ui/DataTable";
import { ContractListResponse } from "../types/response";
import { repaymentTypeConfig } from "../types/repaymentTypeConfig";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import { wonFormat } from "@/shared/utils/wonFormat";
import ContractStatusBadge from "../ui/ContractStatusBadge";

export const contractReceivedColumns: ColumnDef<ContractListResponse>[] = [
  {
    header: "채권자 정보",
    key: "creditorInfo",
    render: (contract: ContractListResponse) => (
      <>
        {contract.name}({truncateAddress(contract.walletAddress)})
      </>
    ),
  },
  {
    header: "대출 금액",
    key: "amount",
    align: "right",
    render: (contract: ContractListResponse) => wonFormat(contract.la),
  },
  {
    header: "이자율",
    key: "interestRate",
    align: "right",
    render: (contract: ContractListResponse) => `${contract.ir}%`,
  },
  {
    header: "상환 방식",
    key: "repaymentType",
    align: "center",
    render: (contract: ContractListResponse) =>
      repaymentTypeConfig[contract.repayType],
  },
  {
    header: "대출 기간",
    key: "loanTerm",
    align: "center",
    render: (contract: ContractListResponse) => `${contract.lt}개월`,
  },
  {
    header: "월 상환일",
    key: "monthlyPaymentDate",
    align: "center",
    render: (contract: ContractListResponse) => `매월 ${contract.mpDt}일`,
  },
  {
    header: "계약 시행일",
    key: "contractDate",
    align: "center",
    render: (contract: ContractListResponse) => contract.contractDt,
  },
  {
    header: "요청일",
    key: "createdAt",
    align: "center",
    render: (contract: ContractListResponse) => contract.createdAt,
  },
  {
    header: "상태",
    key: "status",
    align: "center",
    render: (contract: ContractListResponse) => (
      <div className="flex justify-center">
        <ContractStatusBadge status={contract.contractStatus} />
      </div>
    ),
  },
];
