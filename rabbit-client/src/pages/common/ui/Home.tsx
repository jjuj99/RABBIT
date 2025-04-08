import { withLazyComponent } from "@/widget/common/lib/withLazyComponent";
import { motion } from "framer-motion";
import { MyNFTcard } from "@/entities/NFT/ui/MyNFTcard";
import RAB from "@/shared/ui/RAB";

const RabbitCoin = withLazyComponent(
  () => import("@/entities/common/ui/RabbitCoin"),
);

const Home = () => {
  return (
    <div className="relative flex min-h-screen w-full flex-col items-center justify-center overflow-hidden">
      {/* 배경 요소들 */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1 }}
        className="absolute inset-0"
      >
        <div className="bg-brand-primary/10 absolute top-1/4 left-1/4 h-64 w-64 rounded-full blur-3xl" />
        <div className="bg-brand-primary/5 absolute right-1/4 bottom-1/4 h-96 w-96 rounded-full blur-3xl" />
      </motion.div>

      {/* 메인 콘텐츠 */}
      <div className="relative z-10 flex w-full flex-col items-center">
        {/* 히어로 섹션 */}
        <div className="flex w-full flex-col items-center gap-8 px-4 py-16 sm:gap-12 sm:py-24 md:gap-16 md:py-32">
          {/* 코인 섹션 */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="h-[250px] w-full sm:h-[300px] md:h-[350px] md:w-[700px]"
          >
            <RabbitCoin />
          </motion.div>

          {/* 제목 섹션 */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.4 }}
            className="flex flex-col items-center gap-3 text-center sm:gap-4"
          >
            <h1 className="font-dunggeunmo text-4xl font-bold text-white sm:text-5xl md:text-6xl lg:text-7xl">
              <span className="font-pixel">RABBIT</span>
            </h1>
            <p className="text-lg text-gray-300 sm:text-xl">
              블록체인으로 차용증 거래의 미래를 여는
              <br />
              투명하고 안전한 디지털 자산 플랫폼
            </p>
          </motion.div>
        </div>

        {/* 특징 섹션 */}
        <div className="w-full bg-white/5 py-16 backdrop-blur-sm">
          <div className="mx-auto max-w-7xl px-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: false, margin: "-100px" }}
              transition={{ duration: 0.8 }}
              className="grid w-full grid-cols-1 gap-4 sm:grid-cols-2 sm:gap-6 lg:grid-cols-4"
            >
              {[
                {
                  title: "디지털 자산화",
                  description:
                    "차용증을 NFT로 발행하여 언제든 시장에서 거래 가능",
                  icon: "🎨",
                },
                {
                  title: "실시간 경매",
                  description: "원하는 시점에 경매를 통해 자산을 현금화",
                  icon: "⚡",
                },
                {
                  title: "스테이블 코인",
                  description: "1원 = 1RAB으로 안정적인 가치 보장",
                  icon: "💰",
                },
                {
                  title: "투명한 거래",
                  description: "블록체인 기반으로 모든 거래 내역 공개",
                  icon: "🔍",
                },
              ].map((item, index) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: index % 2 === 0 ? -20 : 20 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  viewport={{ once: false, margin: "-100px" }}
                  transition={{ duration: 0.5, delay: index * 0.1 }}
                  whileHover={{ scale: 1.02, y: -5 }}
                  className="group relative flex h-32 items-center gap-4 rounded-lg bg-white/5 p-4 backdrop-blur-sm sm:h-40 sm:gap-6 sm:p-6"
                >
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-white text-xl sm:h-12 sm:w-12 sm:text-2xl">
                    {item.icon}
                  </div>
                  <div className="flex flex-col gap-1">
                    <h3 className="text-lg font-semibold text-white sm:text-xl">
                      {item.title}
                    </h3>
                    <p className="text-sm text-gray-300 sm:text-base">
                      {item.description}
                    </p>
                  </div>
                </motion.div>
              ))}
            </motion.div>
          </div>
        </div>

        {/* NFT 카드 섹션 */}
        <div className="w-full py-16">
          <div className="mx-auto max-w-7xl px-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: false, margin: "-100px" }}
              transition={{ duration: 0.8 }}
              className="from-brand-primary/10 to-brand-primary/5 relative overflow-hidden rounded-2xl bg-gray-900 p-8 backdrop-blur-xl sm:p-12"
            >
              <div className="absolute inset-0 bg-[url('/grid.svg')] opacity-10" />
              <div className="relative z-10">
                <motion.h2
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: false, margin: "-100px" }}
                  initial={{ opacity: 0, y: 20 }}
                  transition={{ duration: 0.5 }}
                  className="mb-8 text-center text-2xl font-bold text-white sm:mb-12 sm:text-3xl"
                >
                  차용증의 새로운 가치
                </motion.h2>
                <div className="grid grid-cols-1 items-center gap-12 sm:grid-cols-2">
                  <div className="space-y-6">
                    <h3 className="text-2xl font-bold text-white">
                      디지털 자산으로서의 차용증
                    </h3>
                    <p className="text-lg leading-relaxed text-gray-300">
                      차용증을 NFT로 발행하여 디지털 자산으로 변환합니다.
                      언제든지 시장에서 거래할 수 있어 유동성을 확보할 수
                      있습니다.
                    </p>
                    <div className="space-y-3">
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          원하는 시점에 현금화 가능
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          투명한 거래 기록
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          블록체인 기반 보안 시스템
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          실시간 시장 가격 확인
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          스마트 컨트랙트 자동 실행
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          중개 수수료 없음
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="flex justify-center">
                    <div className="w-full max-w-[300px] transform transition-transform hover:scale-105">
                      <MyNFTcard
                        item={{
                          tokenId: "1",
                          crId: 1,
                          crName: "채권자 이름",
                          matDt: "2025-04-06",
                          la: 2000000,
                          ir: 6.5,
                          totalAmount: 20000000,
                          repayType: "원금 균등 상환",
                          dir: 200,
                          earlypayFlag: true,
                          earlypayFee: 50000,
                          creditScore: 750,
                          defCnt: 1,
                        }}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>
          </div>
        </div>

        {/* 스테이블 코인 섹션 */}
        <div className="w-full py-16 backdrop-blur-sm">
          <div className="mx-auto max-w-7xl px-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: false, margin: "-100px" }}
              transition={{ duration: 0.8 }}
              className="relative overflow-hidden rounded-2xl p-8 backdrop-blur-xl sm:p-12"
            >
              <div className="relative z-10">
                <motion.h2
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: false, margin: "-100px" }}
                  initial={{ opacity: 0, y: 20 }}
                  transition={{ duration: 0.5 }}
                  className="mb-8 text-center text-2xl font-bold text-white sm:mb-12 sm:text-3xl"
                >
                  안정적인 가치의 보장
                </motion.h2>
                <div className="grid grid-cols-1 items-center gap-12 sm:grid-cols-2">
                  <div className="space-y-6">
                    <h3 className="text-2xl font-bold text-white">
                      1원 = 1RAB 스테이블 코인
                    </h3>
                    <p className="text-lg leading-relaxed text-gray-300">
                      RAB 코인은 1원과 1:1로 연동된 스테이블 코인입니다. 가치
                      변동 없이 안정적인 거래가 가능합니다.
                    </p>
                    <div className="space-y-3">
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          안정적인 가치 보장
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          투명한 자금 흐름 추적
                        </span>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-brand-primary text-xl">✓</span>
                        <span className="text-base text-gray-300">
                          환율 변동없는 안정적인 코인의 가치
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="flex justify-center">
                    <div className="flex flex-col items-center gap-4">
                      <div className="relative flex h-48 w-48 items-center justify-center">
                        {/* 외부 원형 그라데이션 */}
                        <div className="from-brand-primary/30 to-brand-primary/10 absolute inset-0 rounded-full bg-gradient-to-br blur-xl" />
                        {/* 중앙 콘텐츠 */}
                        <div className="relative flex flex-col items-center gap-2">
                          <RAB amount={1} size="xl" />
                          <div className="flex items-center gap-2">
                            <span className="text-2xl font-bold text-white">
                              =
                            </span>
                            <span className="text-2xl font-bold text-white">
                              1₩
                            </span>
                          </div>
                        </div>
                        {/* 회전하는 원형 효과 */}
                        <div className="animate-spin-slow border-brand-primary/30 absolute inset-0 rounded-full border" />
                        <div
                          className="animate-spin-slow border-brand-primary/20 absolute inset-2 rounded-full border"
                          style={{ animationDirection: "reverse" }}
                        />
                        {/* 강화된 Pulse 효과 */}
                        <div className="bg-brand-primary/20 absolute inset-0 animate-[pulse_3s_ease-in-out_infinite] rounded-full" />
                        <div className="bg-brand-primary/30 absolute inset-4 animate-[pulse_3s_ease-in-out_infinite_0.5s] rounded-full" />
                      </div>
                      <div className="text-center text-sm text-gray-300">
                        안정적인 가치로 거래하세요
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>
          </div>
        </div>

        {/* 미래 비전 섹션 */}
        <div className="w-full py-16">
          <div className="mx-auto max-w-7xl px-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: false, margin: "-100px" }}
              transition={{ duration: 0.8 }}
              className="from-brand-primary/10 to-brand-primary/5 relative overflow-hidden rounded-2xl bg-gray-900 p-4 backdrop-blur-xl sm:p-6 md:p-8"
            >
              <div className="absolute inset-0 bg-[url('/grid.svg')] opacity-10" />
              <div className="relative z-10">
                <motion.h2
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: false, margin: "-100px" }}
                  initial={{ opacity: 0, y: 20 }}
                  transition={{ duration: 0.5 }}
                  className="mb-6 text-center text-2xl font-bold text-white sm:mb-8 sm:text-3xl"
                >
                  차용증 거래의 새로운 혁신
                </motion.h2>
                <div className="space-y-6">
                  <div className="space-y-4">
                    <h3 className="text-xl font-bold text-white">
                      CBDC와 함께하는 미래
                    </h3>
                    <p className="text-gray-400">
                      한국은행의 CBDC 시험 운영에 따라 신한, 국민, 하나, 농협 등
                      주요 은행들이 블록체인 사업에 도전하고 있습니다. 이러한
                      움직임은 우리 서비스의 성장을 위한 발판이 될 것입니다.
                    </p>
                  </div>
                  <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div className="rounded-lg bg-white/5 p-4">
                      <h4 className="mb-2 font-bold text-white">
                        P2P 대출의 진정한 의미
                      </h4>
                      <p className="text-sm text-gray-400">
                        블록체인 기술을 통해 투명하고 안전한 P2P 대출 서비스를
                        제공합니다. 차용증의 디지털 자산화로 새로운 금융 시장을
                        열어갑니다.
                      </p>
                    </div>
                    <div className="rounded-lg bg-white/5 p-4">
                      <h4 className="mb-2 font-bold text-white">
                        미래 금융의 중심
                      </h4>
                      <p className="text-sm text-gray-400">
                        CBDC의 상용화와 함께 우리 서비스는 진정한 의미의 P2P
                        대출 산업으로 성장할 것입니다.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </div>

      {/* 풋터 */}
      <footer className="w-full bg-white/5 backdrop-blur-sm">
        <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
            <div className="space-y-4">
              <h3 className="text-lg font-bold text-white">RABBIT</h3>
              <p className="text-sm text-gray-400">
                블록체인 기반의 P2P 차용증 거래 플랫폼
              </p>
            </div>
            <div className="space-y-4">
              <h3 className="text-lg font-bold text-white">서비스</h3>
              <ul className="space-y-2 text-sm text-gray-400">
                <li>차용증 발행</li>
                <li>실시간 경매</li>
                <li>스마트 컨트랙트</li>
                <li>거래 내역</li>
              </ul>
            </div>
            <div className="space-y-4">
              <h3 className="text-lg font-bold text-white">문의하기</h3>
              <ul className="space-y-2 text-sm text-gray-400">
                <li>이메일: support@rabbit.com</li>
                <li>전화: 02-1234-5678</li>
                <li>주소: 서울특별시 강남구 어느 빌딩의 14층에서..</li>
              </ul>
            </div>
            <div className="space-y-4">
              <h3 className="text-lg font-bold text-white">소셜 미디어</h3>
              <div className="flex space-x-4">
                <a href="#" className="text-gray-400 hover:text-white">
                  <span className="sr-only">Twitter</span>
                  <svg
                    className="h-6 w-6"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84" />
                  </svg>
                </a>
                <a href="#" className="text-gray-400 hover:text-white">
                  <span className="sr-only">GitHub</span>
                  <svg
                    className="h-6 w-6"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fillRule="evenodd"
                      d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"
                      clipRule="evenodd"
                    />
                  </svg>
                </a>
                <a href="#" className="text-gray-400 hover:text-white">
                  <span className="sr-only">LinkedIn</span>
                  <svg
                    className="h-6 w-6"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      fillRule="evenodd"
                      d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z"
                      clipRule="evenodd"
                    />
                  </svg>
                </a>
              </div>
            </div>
          </div>
          <div className="mt-8 border-t border-white/10 pt-8">
            <p className="text-center text-sm text-gray-400">
              © 2025 RABBIT. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;
