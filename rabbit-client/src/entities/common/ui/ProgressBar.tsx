import { useEffect, useState } from "react";

interface ProgressBarProps {
  startDate: string;
  endDate: string;
}

export const ProgressBar = ({ startDate, endDate }: ProgressBarProps) => {
  const [progress, setProgress] = useState(0);
  const [isHovered, setIsHovered] = useState(false);
  const [hoverPosition, setHoverPosition] = useState(0);

  useEffect(() => {
    const calculateProgress = () => {
      const start = new Date(startDate).getTime();
      const end = new Date(endDate).getTime();
      const now = new Date().getTime();

      const totalDuration = end - start;
      const elapsedDuration = now - start;
      const calculatedProgress = Math.min(
        Math.max((elapsedDuration / totalDuration) * 100, 0),
        100,
      );

      setProgress(calculatedProgress);
    };

    calculateProgress();
    const interval = setInterval(calculateProgress, 1000);

    return () => clearInterval(interval);
  }, [startDate, endDate]);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const position = ((e.clientX - rect.left) / rect.width) * 100;
    setHoverPosition(position);
  };

  const calculateRemainingDays = (position: number) => {
    const start = new Date(startDate).getTime();
    const end = new Date(endDate).getTime();
    const totalDuration = end - start;
    const targetTime = start + (totalDuration * position) / 100;
    const now = new Date().getTime();
    const remainingTime = targetTime - now;
    const remainingDays = Math.ceil(remainingTime / (1000 * 60 * 60 * 24));
    return remainingDays;
  };

  return (
    <div className="w-full">
      <div className="mb-2 flex items-center justify-between">
        <span className="text-sm text-gray-400">진행률</span>
        <span className="text-sm font-medium text-white">
          {progress.toFixed(1)}%
        </span>
      </div>
      <div
        className="relative h-2 w-full overflow-hidden rounded-full bg-gray-700"
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        onMouseMove={handleMouseMove}
      >
        <div
          className="bg-brand-primary h-full transition-all duration-500 ease-in-out"
          style={{ width: `${progress}%` }}
        />
        {isHovered && (
          <>
            <div
              className="absolute h-full w-0.5 bg-white"
              style={{ left: `${hoverPosition}%` }}
            />
            <div
              className="absolute -top-8 left-1/2 -translate-x-1/2 rounded-md bg-gray-900 px-2 py-1 text-xs text-white"
              style={{ left: `${hoverPosition}%` }}
            >
              {calculateRemainingDays(hoverPosition)}일 남음
            </div>
          </>
        )}
      </div>
    </div>
  );
};
