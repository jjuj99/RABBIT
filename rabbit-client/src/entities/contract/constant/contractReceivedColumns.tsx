import { ColumnDef } from "@/shared/ui/DataTable";
import { ContractReceivedListResponse } from "../types/response";
import { repaymentTypeConfig } from "../types/repaymentTypeConfig";
import { truncateAddress } from "@/shared/utils/truncateAddress";
import { wonFormat } from "@/shared/utils/wonFormat";
import ContactStatusBadge from "../ui/ContactStatusBadge";

export const contractReceivedColumns: ColumnDef<ContractReceivedListResponse>[] =
  [
    {
      header: "채권자 정보",
      key: "creditorInfo",
      render: (contract: ContractReceivedListResponse) => (
        <>
          {contract.creditorName}({truncateAddress(contract.creditorWallet)})
        </>
      ),
    },
    {
      header: "대출 금액",
      key: "amount",
      align: "right",
      render: (contract: ContractReceivedListResponse) =>
        wonFormat(contract.amount),
    },
    {
      header: "이자율",
      key: "interestRate",
      align: "right",
      render: (contract: ContractReceivedListResponse) =>
        `${contract.interestRate}%`,
    },
    {
      header: "상환 방식",
      key: "repaymentType",
      align: "center",
      render: (contract: ContractReceivedListResponse) =>
        repaymentTypeConfig[contract.repaymentType],
    },
    {
      header: "대출 기간",
      key: "loanTerm",
      align: "center",
      render: (contract: ContractReceivedListResponse) =>
        `${contract.loanTerm}개월`,
    },
    {
      header: "월 상환일",
      key: "monthlyPaymentDate",
      align: "center",
      render: (contract: ContractReceivedListResponse) =>
        `매월 ${contract.monthlyPaymentDate}일`,
    },
    {
      header: "계약 시행일",
      key: "contractDate",
      align: "center",
      render: (contract: ContractReceivedListResponse) => contract.contractDate,
    },
    {
      header: "요청일",
      key: "createdAt",
      align: "center",
      render: (contract: ContractReceivedListResponse) => contract.createdAt,
    },
    {
      header: "상태",
      key: "status",
      align: "center",
      render: (contract: ContractReceivedListResponse) => (
        <div className="flex justify-center">
          <ContactStatusBadge status={contract.status} />
        </div>
      ),
    },
  ];
