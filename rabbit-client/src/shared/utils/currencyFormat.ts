const currencyFormat = (amount: number) => {
  return new Intl.NumberFormat("ko-KR", {
    useGrouping: true,
  }).format(amount);
};

export default currencyFormat;
