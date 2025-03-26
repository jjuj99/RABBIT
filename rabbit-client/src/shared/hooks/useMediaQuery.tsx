import { useEffect, useState } from "react";

type BreakPoint = "sm" | "md" | "lg" | "xl" | "2xl";

const breakpoints = {
  sm: "640px",
  md: "768px",
  lg: "1024px",
  xl: "1280px",
  "2xl": "1536px",
} as const;

export const useMediaQuery = (
  breakpoint: BreakPoint,
  type: "min" | "max" = "min",
) => {
  const [matches, setMatches] = useState(false);

  useEffect(() => {
    const query = `(${type}-width: ${breakpoints[breakpoint]})`;
    const media = window.matchMedia(query);

    setMatches(media.matches);

    const listener = () => setMatches(media.matches);
    media.addEventListener("change", listener);
    return () => media.removeEventListener("change", listener);
  }, [breakpoint, type]);

  return matches;
};

export default useMediaQuery;
