import { useEffect, useRef } from "react";

const SignatureLoader = () => {
  const svgRef = useRef<SVGSVGElement | null>(null);
  const TOTAL_TIME = 3000; // 모든 선이 마무리되는 총 시간 (ms)

  useEffect(() => {
    if (!svgRef.current) return;
    const paths = Array.from(svgRef.current.querySelectorAll("path, line")) as (
      | SVGPathElement
      | SVGLineElement
    )[];
    const lengths = paths.map((path) => path.getTotalLength());
    const maxLength = Math.max(...lengths);

    // 각 path에 대해, 길이에 따른 duration과 delay를 계산합니다.
    // duration = (현재 path 길이 / maxLength) * TOTAL_TIME
    // delay = TOTAL_TIME - duration (즉, 가장 긴 path는 delay 0, 나머지는 나중에 시작)
    paths.forEach((path, index) => {
      const length = lengths[index];
      const duration = (length / maxLength) * TOTAL_TIME;
      const delay = TOTAL_TIME - duration;

      const runAnimation = () => {
        // 애니메이션 시작 전에 초기 상태로 리셋
        path.style.strokeDasharray = `${length}`;
        path.style.strokeDashoffset = `${length}`;

        const animation = path.animate(
          [{ strokeDashoffset: length }, { strokeDashoffset: 0 }],
          {
            duration: duration,
            delay: delay,
            easing: "ease-in-out",
            fill: "none", // 애니메이션 완료 후 원래 상태로 되돌리기 위해 fill을 none으로 설정
          },
        );

        animation.finished.then(() => {
          // RESET_DELAY 후 애니메이션을 다시 시작하여 반복
          setTimeout(() => {
            runAnimation();
          });
        });
      };

      runAnimation();
    });
  }, []);

  return (
    <div style={{ position: "relative", width: "400px", height: "400px" }}>
      <svg
        ref={svgRef}
        viewBox="0 0 310 333"
        xmlns="http://www.w3.org/2000/svg"
        style={{ width: "100%", height: "100%" }}
      >
        <defs>
          <filter id="glow">
            <feGaussianBlur stdDeviation="2" result="coloredBlur" />
            <feMerge>
              <feMergeNode in="coloredBlur" />
              <feMergeNode in="SourceGraphic" />
            </feMerge>
          </filter>
        </defs>
        <g id="_레이어_1-2" data-name="레이어_1">
          <path
            className="cls-6"
            d="M217.88,181.86s8.16-35.16,36-34.43,1.89,19.34-8.56,25.7c-11.39,6.93-21.76,10.35-25.9,10.43-.92.02-1.64-.78-1.54-1.7,3.2-28.76-42.53-33.96-60.32-27.52s-1.38,30.22,7.91-1.38c10.2-34.7-16.85-45.12-26.38-47.63-1.26-.33-1.73-1.88-.84-2.84,11.55-12.43,39.53-69.74,21.73-74.72-23.49-6.56-47.29,60.05-52.3,71.41-.09.19-.38.1-.33-.11,2.37-11.12,14.5-71.95-4.17-68.03-20.55,4.32-25.73,41.09-25.73,41.09-2.76,27.28,6.73,38.5,6.73,38.5-26.59,15.71-28.49,40.06-28.49,40.06-.35,37.64,43.51,37.12,43.51,37.12"
            stroke="white"
            fill="none"
            strokeWidth="8"
            filter="url(#glow)"
          />
        </g>
      </svg>
    </div>
  );
};

export default SignatureLoader;
