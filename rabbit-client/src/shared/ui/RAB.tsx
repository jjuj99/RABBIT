const RAB = ({
  amount,
  size = "lg",
  isColored = true,
}: {
  amount: number;
  size?: "lg" | "sm" | "md";
  isColored?: boolean;
}) => {
  const formattedAmount = amount.toLocaleString();

  const amountSize = {
    lg: "text-xl",
    md: "text-lg",
    sm: "text-base",
  }[size];

  const rabSize = {
    lg: "text-sm",
    md: "text-xs",
    sm: "text-xs",
  }[size];

  return (
    <div>
      <span
        className={`${isColored ? "text-brand-gradient" : ""} font-partial ${amountSize}`}
      >
        {formattedAmount}
      </span>
      <span className={`text-brand-primary font-pixel ${rabSize}`}>RAB</span>
    </div>
  );
};

export default RAB;
