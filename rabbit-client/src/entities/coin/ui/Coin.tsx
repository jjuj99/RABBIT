import { useFrame } from "@react-three/fiber";
import { useEffect, useRef, useState } from "react";
import { Group, Mesh, Vector3, DoubleSide } from "three";
import createSleekGridTexture from "./createSleekGridTexture";
import createSleekGridBackTexture from "./createSleekGridBackTexture";
import createLogoAlphaMap from "./createLogoAlphaMap";
import { CanvasTexture } from "three";

interface CoinProps {
  position?: [number, number, number];
  scale?: number;
}

const Coin = ({ position = [0, 0, 0], scale = 1 }: CoinProps) => {
  const groupRef = useRef<Group>(null);
  const diskRef = useRef<Mesh>(null);
  const [textures, setTextures] = useState<{
    logo: CanvasTexture | null;
    back: CanvasTexture | null;
    alpha: CanvasTexture | null;
  }>({ logo: null, back: null, alpha: null });

  // 초기 각도 설정을 위한 참조
  const initialRotation = useRef({
    x: Math.PI / 2 - 0.4, // 살짝 위쪽으로 기울임
    y: -0.3, // 살짝 왼쪽으로 기울임
    z: 0.4, // 약간의 회전 추가
  });

  useEffect(() => {
    const logoTexture = createSleekGridTexture();
    const backTexture = createSleekGridBackTexture();
    const logoAlphaMap = createLogoAlphaMap();
    setTextures({ logo: logoTexture, back: backTexture, alpha: logoAlphaMap });
  }, []);

  useEffect(() => {
    if (groupRef.current) {
      groupRef.current.rotation.x = initialRotation.current.x;
      groupRef.current.rotation.y = initialRotation.current.y;
      groupRef.current.rotation.z = initialRotation.current.z;
    }
  }, []);

  // 간헐적 회전 애니메이션
  const animState = useRef({
    isRotating: false,
    rotationProgress: 0,
    startTime: 0,
    nextRotationTime: 5,
    rotationAxis: new Vector3(0, 0, 1),
    rotationDuration: 0,
    startRotation: { x: 0, y: 0, z: 0 },
  });

  useFrame((state) => {
    if (!groupRef.current) return;

    const t = state.clock.getElapsedTime();

    // 미세한 움직임 유지
    groupRef.current.position.y = Math.sin(t * 0.5) * 0.1;

    // 회전 애니메이션 상태 관리
    if (!animState.current.isRotating) {
      // 비회전 상태 - 다음 회전 시작 시간 체크
      if (t > animState.current.nextRotationTime) {
        // 회전 시작
        animState.current.isRotating = true;
        animState.current.rotationProgress = 0;
        animState.current.startTime = t;

        // 현재 각도를 저장하여 시작점으로 사용
        animState.current.startRotation = {
          x: groupRef.current.rotation.x,
          y: groupRef.current.rotation.y,
          z: groupRef.current.rotation.z,
        };

        // 회전 축을 z축으로 고정
        animState.current.rotationAxis.set(0, 0, 1);

        // 회전 시간 설정 (1.5-2.5초)
        animState.current.rotationDuration = 1.5 + Math.random();
      } else {
        // 비회전 상태에서는 코인이 약간 비스듬히 위치하도록
        const tiltAngleY = Math.sin(t * 0.1) * 0.15; // Y축 기울기 (감소)
        const tiltAngleX = Math.cos(t * 0.08) * 0.1; // X축 기울기

        // 기본 방향은 초기 설정된 각도를 기준으로 약간씩 변화
        groupRef.current.rotation.x = initialRotation.current.x + tiltAngleX;
        groupRef.current.rotation.y =
          initialRotation.current.y + tiltAngleY * 0.5;
        groupRef.current.rotation.z =
          initialRotation.current.z + tiltAngleY * 0.3;
      }
    } else {
      // 회전 중인 상태
      const elapsed = t - animState.current.startTime;
      const duration = animState.current.rotationDuration;

      if (elapsed < duration) {
        // 애니메이션 진행 중 (부드러운 회전을 위해 사인 곡선 사용)
        const progress = elapsed / duration;
        const easedProgress = (1 - Math.cos(progress * Math.PI)) / 2; // 이징 함수

        // 처음과 마지막에는 천천히, 중간에는 빠르게 회전
        const angle = easedProgress * Math.PI * 2; // 한 바퀴

        // Z축 회전 적용 (동전이 앞뒤로 뒤집히는 효과)
        // 시작 회전값에서부터 회전
        groupRef.current.rotation.x = animState.current.startRotation.x;
        groupRef.current.rotation.y = animState.current.startRotation.y;
        groupRef.current.rotation.z = animState.current.startRotation.z + angle;
      } else {
        // 회전 완료 - 다음 회전으로 부드럽게 전환
        animState.current.isRotating = false;
        animState.current.nextRotationTime = t + 7 + Math.random() * 5; // 7~12초 후 다음 회전

        // 모듈로 연산을 통해 2π 간격으로 각도 정규화
        groupRef.current.rotation.z =
          animState.current.startRotation.z + Math.PI * 2;
        groupRef.current.rotation.z %= Math.PI * 2;
      }
    }
  });

  // 조건부 렌더링은 마지막에
  if (!textures.logo || !textures.back || !textures.alpha) {
    return null;
  }

  return (
    <group ref={groupRef} position={position} scale={scale}>
      {/* 코인 디스크 - 매우 얇게 수정 */}
      <mesh ref={diskRef} receiveShadow castShadow>
        <cylinderGeometry args={[10, 10, 0.4, 64]} />
        <meshStandardMaterial
          color="#151515"
          metalness={0.95}
          roughness={0.1}
          envMapIntensity={1.0}
        />
      </mesh>
      {/* 앞면 - 로고 */}
      <mesh position={[0, 0.21, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <circleGeometry args={[10, 64]} />
        <meshBasicMaterial map={textures.logo} transparent={true} />
      </mesh>
      {/* 앞면 - 돌출 로고(텍스트 제외) */}
      <mesh position={[0, 0.23, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <circleGeometry args={[9.8, 64]} />
        <meshStandardMaterial
          alphaMap={textures.alpha}
          alphaTest={0.1}
          color="#DDDDDD"
          metalness={0.95}
          roughness={0.1}
          emissive="#222222"
          emissiveIntensity={0.1}
          displacementMap={textures.alpha}
          displacementScale={0.15}
        />
      </mesh>
      {/* 뒷면 - 심플한 블록체인 디자인 */}
      <mesh position={[0, -0.21, 0]} rotation={[Math.PI / 2, 0, Math.PI]}>
        <circleGeometry args={[10, 64]} />
        <meshBasicMaterial map={textures.back} transparent={true} />
      </mesh>
      {/* 가장자리 - 두껍고 메탈릭한 엣지 */}
      <mesh position={[0, 0, 0]}>
        <cylinderGeometry args={[10.2, 10.2, 0.8, 64, 1, true]} />
        <meshStandardMaterial
          color="#222222"
          metalness={0.9}
          roughness={0.2}
          side={DoubleSide}
        />
      </mesh>
      // 테두리 링도 함께 수정
      {/* 얇은 테두리 링 (상단) */}
      <mesh position={[0, 0.26, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <ringGeometry args={[9.9, 10.2, 64]} />
        <meshStandardMaterial color="#333333" metalness={1.0} roughness={0.1} />
      </mesh>
      {/* 얇은 테두리 링 (하단) */}
      <mesh position={[0, -0.26, 0]} rotation={[Math.PI / 2, 0, 0]}>
        <ringGeometry args={[9.9, 10.2, 64]} />
        <meshStandardMaterial color="#333333" metalness={1.0} roughness={0.1} />
      </mesh>
    </group>
  );
};

export default Coin;
