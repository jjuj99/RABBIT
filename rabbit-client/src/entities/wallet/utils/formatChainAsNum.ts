// 체인 ID 포맷팅
export const formatChainAsNum = (chainIdHex: string) => {
  const chainIdNum = parseInt(chainIdHex);
  return chainIdNum;
};

export default formatChainAsNum;
