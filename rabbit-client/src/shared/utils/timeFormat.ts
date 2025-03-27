const timeFormat = (date: string) => {
  return new Date(date)
    .toLocaleTimeString("ko-KR", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    })
    .replace(".", "");
};

export default timeFormat;
