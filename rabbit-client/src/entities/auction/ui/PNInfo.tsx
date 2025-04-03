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
    <div className="flex h-fit w-[170px] flex-col gap-0 rounded-sm bg-gray-800 p-3 sm:w-[248px] sm:gap-2 sm:p-4">
      <h2 className="text-sm text-gray-200 sm:text-base">{title}</h2>
      <div className="text font-medium whitespace-nowrap text-white sm:text-xl">
        {value}
      </div>
    </div>
  );
};

export default PNInfo;
