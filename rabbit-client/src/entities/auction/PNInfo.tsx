interface PNInfoProps {
  title: string;
  value: string;
}

// sm: "640px",
//   md: "768px",
//   lg: "1024px",
//   xl: "1280px",

const PNInfo = ({ title, value }: PNInfoProps) => {
  return (
    <div className="flex h-fit w-[170px] flex-col gap-0 rounded-sm bg-gray-700 p-3 sm:w-[248px] sm:gap-2 sm:p-4">
      <h2 className="sm:text text-sm text-gray-200">{title}</h2>
      <div className="text font-medium text-white sm:text-2xl">{value}</div>
    </div>
  );
};

export default PNInfo;
