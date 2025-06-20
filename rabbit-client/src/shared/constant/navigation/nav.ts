export const NAV_ITEMS = [
  {
    to: "/contract/new",
    label: "차용증",
    activeLabel: "contract",
  },

  {
    to: "/loan/borrow",
    label: "채권\u2022채무",
    activeLabel: "loan",
  },
  {
    to: "/account/charge",
    label: "입\u2022출금",
    activeLabel: "account",
  },
  {
    to: "/auction/list",
    label: "경매",
    activeLabel: "auction",
  },
];

export const SUB_NAV_ITEMS = {
  contract: [
    { label: "차용증 작성", to: "contract/new" },
    { label: "보낸 요청", to: "contract/sent" },
    { label: "받은 요청", to: "contract/received" },
  ],
  auction: [
    { label: "차용증 경매", to: "auction/list" },
    { label: "입찰 내역", to: "auction/history" },
  ],
  loan: [
    { label: "나의 채무", to: "loan/borrow" },
    { label: "나의 채권", to: "loan/lent" },
  ],
  // account: [
  //   { label: "충전", to: "account/charge" },
  //   { label: "출금", to: "account/withdraw" },
  // ],
};

export const MobileNAV_ITEMS = [
  {
    to: "/",
    label: "홈",
    activeLabel: "home",
    icon: "/icons/home.svg",
  },
  {
    to: "/contract/new",
    label: "차용증 작성",
    activeLabel: "contract",
    icon: "/icons/pen.svg",
  },
  {
    to: "/auction/list",
    label: "차용증 경매",
    activeLabel: "auction",
    icon: "/icons/auction.svg",
  },
  {
    to: "/loan/borrow",
    label: "채권\u2022채무",
    activeLabel: "loan",
    icon: "/icons/paper.svg",
  },
  {
    to: "/account/charge",
    label: "입\u2022출금",
    activeLabel: "account",
    icon: "/icons/bank.svg",
  },
];
