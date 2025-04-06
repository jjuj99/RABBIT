import Coin from "@/entities/coin/ui/Coin";
import Lights from "@/entities/coin/ui/Lights";
import { Environment, OrbitControls } from "@react-three/drei";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import { Bloom, EffectComposer } from "@react-three/postprocessing";
import { useEffect, useRef, useState } from "react";
import * as THREE from "three";

// 코인 컨테이너 컴포넌트
const CoinContainer = () => {
  const { mouse, viewport } = useThree();
  const groupRef = useRef<THREE.Group>(null);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768);
    };

    checkMobile();
    window.addEventListener("resize", checkMobile);
    return () => window.removeEventListener("resize", checkMobile);
  }, []);

  useFrame(() => {
    if (!groupRef.current) return;

    // 마우스 위치를 -1에서 1 사이의 값으로 정규화
    const x = (mouse.x * viewport.width) / 2;
    const y = (mouse.y * viewport.height) / 2;

    // 부드러운 회전을 위한 보간
    groupRef.current.rotation.y = THREE.MathUtils.lerp(
      groupRef.current.rotation.y,
      x * 0.05,
      0.05,
    );
    groupRef.current.rotation.x = THREE.MathUtils.lerp(
      groupRef.current.rotation.x,
      -y * 0.05,
      0.05,
    );
  });

  return (
    <group ref={groupRef}>
      <Coin scale={isMobile ? 1.5 : 1.5} />
    </group>
  );
};

// 메인 코인 컴포넌트
const RabbitCoin = () => {
  return (
    <div className="h-full w-full">
      <Canvas
        shadows
        camera={{
          position: [0, 10, 25],
          // fov: 60,
        }}
      >
        <Lights />
        <CoinContainer />
        <OrbitControls
          enablePan={false}
          enableZoom={true}
          minDistance={10}
          maxDistance={30}
          autoRotate={false}
        />
        <Environment preset="warehouse" />
        <EffectComposer>
          <Bloom
            intensity={0.2}
            luminanceThreshold={0.2}
            luminanceSmoothing={0.9}
          />
        </EffectComposer>
      </Canvas>
    </div>
  );
};

export default RabbitCoin;
