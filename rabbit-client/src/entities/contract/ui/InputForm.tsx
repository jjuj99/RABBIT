import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";

import { cn } from "@/shared/lib/utils";
import { FormControl, FormItem, FormLabel } from "@/shared/ui/form";
import { UnitInput } from "@/entities/common";

interface InputFormProps {
  label: string;
  id: string;
  min?: number;
  type: HTMLInputElement["type"];
  placeholder?: string;
  readOnly?: boolean;
  unit?: string;
  className?: string;
  buttonText?: string;
  onClick?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onInputClick?: (e: React.MouseEvent<HTMLInputElement>) => void;
  asChild?: boolean;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

const InputForm = ({
  label,
  id,
  type,
  min,
  unit,
  className,
  placeholder,
  readOnly = false,
  onClick,
  onInputClick,
  buttonText,
  asChild = false,
  onChange,
  ...props
}: InputFormProps) => {
  return (
    <FormItem className={cn("flex w-full flex-col", className)}>
      <div className="flex items-center justify-between">
        <FormLabel className="text-lg md:text-xl" htmlFor={id}>
          {label}
        </FormLabel>
        {/* <FormMessage /> */}
      </div>
      <FormControl>
        <div className="flex gap-3">
          {unit ? (
            <UnitInput
              unit={unit}
              type={type}
              id={id}
              placeholder={placeholder}
              {...props}
              onChange={onChange}
            />
          ) : (
            <Input
              min={min}
              onClick={onInputClick}
              readOnly={readOnly}
              id={id}
              placeholder={placeholder}
              type={type}
              {...props}
              onChange={onChange}
            />
          )}

          {onClick && (
            <Button
              type="button"
              variant="gradient"
              onClick={onClick}
              asChild={asChild}
            >
              {asChild ? <div>{buttonText}</div> : buttonText}
            </Button>
          )}
        </div>
      </FormControl>
    </FormItem>
  );
};

export default InputForm;
