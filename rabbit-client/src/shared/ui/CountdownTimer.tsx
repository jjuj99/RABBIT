import useCountdown from "../hooks/useCountdown";

function CountdownTimer({ endDate }: { endDate: string }) {
  const time = useCountdown(endDate);

  return <span>{time}</span>;
}

export default CountdownTimer;
