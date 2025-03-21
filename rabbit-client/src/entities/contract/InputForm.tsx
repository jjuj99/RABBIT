import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { Label } from "@/shared/ui/label";
import UnitInput from "../common/ui/UnitInput";
import { cn } from "@/shared/lib/utils";

interface InputFormProps {
  label: string;
  id: string;
  type: HTMLInputElement["type"];
  placeholder?: string;
  readOnly?: boolean;
  unit?: string;
  className?: string;
  buttonText?: string;
  onClick?: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const InputForm = ({
  label,
  id,
  type,
  unit,
  className,
  placeholder,
  readOnly = false,
  onClick,
  buttonText,
}: InputFormProps) => {
  return (
    <div className={cn("flex w-full flex-col", className)}>
      <Label className="text-xl" htmlFor={id}>
        {label}
      </Label>
      <div className="flex gap-3">
        {unit ? (
          <UnitInput unit={unit} type={type} id={id} />
        ) : (
          <Input
            readOnly={readOnly}
            id={id}
            placeholder={placeholder}
            type={type}
          />
        )}
        {onClick && (
          <Button type="button" variant="gradient" onClick={onClick}>
            {buttonText}
          </Button>
        )}
      </div>
    </div>
  );
};

export default InputForm;
