import { useRef } from "react";
import { DirectionalLight, PointLight } from "three";

const Lights = () => {
  const directionalLightRef = useRef<DirectionalLight>(null);
  const pointLightRef = useRef<PointLight>(null);

  return (
    <>
      <ambientLight intensity={0.2} /> {/* 매우 낮은 주변광 */}
      <directionalLight
        ref={directionalLightRef}
        position={[10, 10, 5]}
        intensity={1}
        castShadow
        shadow-mapSize-width={2048}
        shadow-mapSize-height={2048}
        shadow-camera-far={50}
        shadow-camera-left={-10}
        shadow-camera-right={10}
        shadow-camera-top={10}
        shadow-camera-bottom={-10}
      />
      <spotLight
        position={[0, 0, 10]}
        angle={0.3}
        penumbra={0.8}
        intensity={0.4}
        castShadow
      />
      {/* 뒷면에도 약간의 빛을 주기 위한 보조 라이트 */}
      <pointLight
        ref={pointLightRef}
        position={[-10, -10, -5]}
        intensity={0.5}
        castShadow
      />
      {/* 엣지를 강조하기 위한 림 라이트 */}
      <pointLight position={[0, 0, -10]} intensity={0.1} color="#FFFFFF" />
    </>
  );
};

export default Lights;
