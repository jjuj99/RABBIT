import { CompleteContractResponse } from "@/entities/contract/types/response";
import { Button } from "@/shared/ui/button";
import RAB from "@/shared/ui/RAB";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router";
import Particles, { initParticlesEngine } from "@tsparticles/react";
import { loadSlim } from "@tsparticles/slim";
import { Separator } from "@/shared/ui/Separator";
import addNFTToWallet from "@/entities/wallet/utils/addNFTToWallet";

const ContractComplete = () => {
  const navigate = useNavigate();
  const { state } = useLocation();
  const [init, setInit] = useState(false);
  const [showParticles, setShowParticles] = useState(false);
  const [showEnhancedGlow, setShowEnhancedGlow] = useState(false);
  console.log(state);
  useEffect(() => {
    addNFTToWallet(state.tokenId, state.nftImageUrl);
  }, [state]);
  useEffect(() => {
    initParticlesEngine(async (engine) => {
      await loadSlim(engine);
    }).then(() => {
      setInit(true);
    });
  }, []);

  useEffect(() => {
    if (!init) return;

    const timer = setTimeout(() => {
      setShowParticles(true);

      setTimeout(() => {
        setShowParticles(false);
        setShowEnhancedGlow(true);

        setTimeout(() => {
          setShowEnhancedGlow(false);
        }, 1500);
      }, 3000);
    }, 2500);

    return () => clearTimeout(timer);
  }, [init]);

  const contract = state as CompleteContractResponse;

  return (
    <main className="relative mt-20 flex h-full flex-col items-center justify-center gap-9">
      {showParticles && (
        <Particles
          id="tsparticles"
          className="fixed inset-0 z-50"
          options={{
            fullScreen: false,
            particles: {
              number: { value: 100 },
              color: { value: ["#FFD700", "#FFF", "#00ff66"] },
              shape: { type: "circle" },
              opacity: {
                value: 1,
                animation: {
                  enable: true,
                  speed: 2,
                  sync: false,
                },
              },
              size: {
                value: 4,
                animation: {
                  enable: true,
                  speed: 5,
                  sync: false,
                },
              },
              move: {
                enable: true,
                speed: 10,
                direction: "outside",
                random: true,
                straight: false,
                outModes: "split",
              },
            },
            emitters: [
              {
                position: { x: 50, y: 50 },
                rate: {
                  delay: 0,
                  quantity: 100,
                },
                size: {
                  width: 0,
                  height: 0,
                },
              },
            ],
          }}
        />
      )}

      <div
        className={`animate-card-entrance group bg-black-glass border-white-glass shadow-glow relative z-[999] flex h-fit w-[326px] flex-col items-center gap-3 rounded-lg border px-3 pt-4 pb-7 transition-transform duration-700 ease-in-out hover:translate-y-[-4px] md:w-[300px] 2xl:w-[326px]`}
      >
        {showEnhancedGlow && (
          <div className="absolute inset-[-2px] animate-[enhancedGlow_1.5s_ease-out] rounded-lg bg-white/20" />
        )}
        <div className="relative h-full w-full rounded-sm">
          <img src="/images/NFT.png" alt="NFT" />
        </div>

        <div className="bg-radial-accent flex w-full flex-col items-center gap-1 rounded-sm px-6 py-3">
          <div className="flex w-full justify-between">
            <span>채권자</span>
            <span className="font-bold">{contract.crName}</span>
          </div>
          <div className="flex w-full justify-between">
            <span>채무액</span>
            <RAB amount={contract.la} size="sm" isColored={false} />
          </div>
          <div className="flex w-full justify-between">
            <span>이자율</span>
            <span>{contract.ir}%</span>
          </div>
          <Separator className="w-full" />
          <div className="flex w-full justify-between">
            <span>월 납부액</span>
            <span>{contract.ir}</span>
          </div>
        </div>
      </div>

      <div className="animate-text-entrance flex flex-col items-center gap-3">
        <span className="text-2xl font-bold">
          계약이 성공적으로 체결되었습니다!
        </span>
        <span className="text-lg">RABBIT #{contract.contractId}</span>
      </div>

      <Button
        className="animate-button-entrance h-11 w-full max-w-[300px] text-xl"
        variant="primary"
        onClick={() => navigate("/loan/lent")}
      >
        채권 관리로 이동
      </Button>
    </main>
  );
};

export default ContractComplete;
