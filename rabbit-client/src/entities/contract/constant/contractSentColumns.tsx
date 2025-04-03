import { truncateAddress } from "@/shared/utils/truncateAddress";
import { ContractListResponse } from "../types/response";
import ContactStatusBadge from "../ui/ContactStatusBadge";
import { wonFormat } from "@/shared/utils/wonFormat";
import { ColumnDef } from "@/shared/ui/DataTable";
import { repaymentTypeConfig } from "../types/repaymentTypeConfig";

export const contractSentColumns: ColumnDef<ContractListResponse>[] = [
  {
    header: "채무자 정보",
    key: "debtorInfo",
    render: (contract: ContractListResponse) => (
      <>
        {contract.debtorName}({truncateAddress(contract.debtorWallet)})
      </>
    ),
  },
  {
    header: "대출 금액",
    key: "amount",
    align: "right",
    render: (contract: ContractListResponse) => wonFormat(contract.amount),
  },
  {
    header: "이자율",
    key: "interestRate",
    align: "right",
    render: (contract: ContractListResponse) => `${contract.interestRate}%`,
  },
  {
    header: "상환 방식",
    key: "repaymentType",
    align: "center",
    render: (contract: ContractListResponse) =>
      repaymentTypeConfig[contract.repaymentType],
  },
  {
    header: "대출 기간",
    key: "loanTerm",
    align: "center",
    render: (contract: ContractListResponse) => `${contract.loanTerm}개월`,
  },
  {
    header: "월 상환일",
    key: "monthlyPaymentDate",
    align: "center",
    render: (contract: ContractListResponse) =>
      `매월 ${contract.monthlyPaymentDate}일`,
  },
  {
    header: "계약 시행일",
    key: "contractDate",
    align: "center",
    render: (contract: ContractListResponse) => contract.contractDate,
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
        <ContactStatusBadge status={contract.status} />
      </div>
    ),
  },
];
