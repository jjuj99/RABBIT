import { useState, ReactNode } from "react";

interface InfoRowProps {
  label: string;
  value: string | ReactNode;
}

export const InfoRow = ({ label, value }: InfoRowProps) => {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div className="flex flex-row items-center justify-between">
      <span className="text-sm font-light text-gray-100 sm:text-base">
        {label}
      </span>
      <div className="relative">
        <span
          className="cursor-help text-sm font-medium text-white sm:text-base"
          onMouseEnter={() => setIsHovered(true)}
          onMouseLeave={() => setIsHovered(false)}
        >
          {value}
        </span>
        {isHovered && (
          <div className="absolute top-full right-0 z-10 mt-1 rounded-md bg-gray-900 px-2 py-1 text-sm whitespace-nowrap text-white shadow-lg">
            {label}
          </div>
        )}
      </div>
    </div>
  );
};
