import { FormItem, FormLabel } from "@/shared/ui/form";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/ui/select";

const SelectRepayType = ({ ...props }) => {
  return (
    <FormItem>
      <div className="flex items-center justify-between">
        <FormLabel className="text-xl">상환방식</FormLabel>
        {/* <FormMessage /> */}
      </div>
      <Select name="REPAY_TYPE" {...props}>
        <SelectTrigger className="w-full bg-gray-600 text-base">
          <SelectValue placeholder="상환방식" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="EPIP">원리금 균등 상환</SelectItem>
          <SelectItem value="EPP">원금 균등 상환</SelectItem>
          <SelectItem value="BP">만기 일시 상환</SelectItem>
        </SelectContent>
      </Select>
    </FormItem>
  );
};

export default SelectRepayType;
