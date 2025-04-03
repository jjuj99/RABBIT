import { cn } from "@/shared/lib/utils";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/ui/table";

// 공통 타입 정의
export interface ColumnDef<T> {
  header: string;
  key: keyof T | string;
  align?: "left" | "center" | "right";
  render?: (data: T) => React.ReactNode;
}

interface DataTableProps<T> {
  columns: ColumnDef<T>[];
  data: T[];
  onRowClick?: (item: T) => void;
}

export function DataTable<T>({ columns, data, onRowClick }: DataTableProps<T>) {
  return (
    <div className="overflow-hidden rounded-lg border border-gray-800 bg-gray-800/50 shadow-lg backdrop-blur-sm">
      <div className="overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow className="border-gray-700">
              {columns.map((column) => (
                <TableHead
                  key={column.key.toString()}
                  className={cn("px-4 py-3 whitespace-nowrap text-white/80", {
                    "text-left": column.align === "left" || !column.align,
                    "text-center": column.align === "center",
                    "text-right": column.align === "right",
                  })}
                >
                  {column.header}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((item, index) => (
              <TableRow
                key={index}
                onClick={() => onRowClick?.(item)}
                className="cursor-pointer border-gray-700/50 text-white transition-colors hover:bg-gray-700/30"
              >
                {columns.map((column) => (
                  <TableCell
                    key={column.key.toString()}
                    className={cn("px-4 py-3", {
                      "text-left": column.align === "left" || !column.align,
                      "text-center": column.align === "center",
                      "text-right": column.align === "right",
                    })}
                  >
                    {column.render
                      ? column.render(item)
                      : String(item[column.key as keyof T])}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
