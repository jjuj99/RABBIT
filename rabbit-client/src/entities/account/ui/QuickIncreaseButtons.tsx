import { Button } from "@/shared/ui/button";

const QuickIncreaseButtons = ({
  setState,
}: {
  setState: React.Dispatch<React.SetStateAction<number>>;
}) => {
  return (
    <div className="hidden gap-2 md:flex">
      <Button
        variant="gradient"
        onClick={() => setState((prev) => prev + 10000)}
      >
        +1만
      </Button>
      <Button
        variant="gradient"
        onClick={() => setState((prev) => prev + 100000)}
      >
        +10만
      </Button>
      <Button
        variant="gradient"
        onClick={() => setState((prev) => prev + 1000000)}
      >
        +100만
      </Button>
      <Button
        variant="gradient"
        onClick={() => setState((prev) => prev + 10000000)}
      >
        +1000만
      </Button>
    </div>
  );
};

export default QuickIncreaseButtons;
