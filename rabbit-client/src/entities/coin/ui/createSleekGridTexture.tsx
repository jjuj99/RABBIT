import { CanvasTexture } from "three";

// 시크하고 격자 패턴이 있는 코인 앞면 텍스처 생성 함수
const createSleekGridTexture = () => {
  const canvas = document.createElement("canvas");
  canvas.width = 1024;
  canvas.height = 1024;
  const ctx = canvas.getContext("2d");

  if (!ctx) return null;

  // 배경 그라데이션
  const centerX = canvas.width / 2;
  const centerY = canvas.height / 2;

  // 어두운 배경 그라데이션
  const bgGradient = ctx.createRadialGradient(
    centerX,
    centerY,
    0,
    centerX,
    centerY,
    500,
  );
  bgGradient.addColorStop(0, "#101010");
  bgGradient.addColorStop(1, "#070707");

  ctx.fillStyle = bgGradient;
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // 세련된 격자 패턴 추가
  ctx.strokeStyle = "#1A1A1A"; // 매우 어두운 회색
  ctx.lineWidth = 1;

  // 수평 격자선
  for (let y = 0; y < canvas.height; y += 30) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(canvas.width, y);
    ctx.stroke();
  }

  // 수직 격자선
  for (let x = 0; x < canvas.width; x += 30) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x, canvas.height);
    ctx.stroke();
  }

  // 동적인 선 패턴 추가
  ctx.strokeStyle = "#222222";
  ctx.lineWidth = 2;

  // 랜덤 방향 선들
  for (let i = 0; i < 15; i++) {
    const x1 = Math.random() * canvas.width;
    const y1 = Math.random() * canvas.height;
    const angle = Math.random() * Math.PI * 2;
    const length = 200 + Math.random() * 400;

    const x2 = x1 + Math.cos(angle) * length;
    const y2 = y1 + Math.sin(angle) * length;

    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.stroke();
  }

  // 직선 교차하는 디자인 추가
  ctx.strokeStyle = "#2A2A2A";
  ctx.lineWidth = 1;

  for (let i = 0; i < 10; i++) {
    // 동서 방향
    const y = Math.random() * canvas.height;
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(canvas.width, y);
    ctx.stroke();

    // 남북 방향
    const x = Math.random() * canvas.width;
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x, canvas.height);
    ctx.stroke();
  }

  // 외부 테두리 원 (얇고 미니멀하게)
  ctx.beginPath();
  ctx.arc(centerX, centerY, 480, 0, Math.PI * 2);
  ctx.lineWidth = 1;
  ctx.strokeStyle = "#333333";
  ctx.stroke();

  // 내부 원형 테두리 (얇게)
  ctx.beginPath();
  ctx.arc(centerX, centerY, 440, 0, Math.PI * 2);
  ctx.lineWidth = 1;
  ctx.strokeStyle = "#333333";
  ctx.stroke();

  // 로고 배경 원 준비 (미묘한 그라데이션)
  ctx.save();
  ctx.beginPath();
  ctx.arc(centerX, centerY, 200, 0, Math.PI * 2);

  // 로고 배경 그라데이션 (매우 미묘하게)
  const logoBgGradient = ctx.createRadialGradient(
    centerX,
    centerY,
    0,
    centerX,
    centerY,
    200,
  );
  logoBgGradient.addColorStop(0, "#181818");
  logoBgGradient.addColorStop(1, "#0A0A0A");

  ctx.fillStyle = logoBgGradient;
  ctx.fill();
  ctx.restore();

  // 로고 스케일 및 위치 계산
  const logoScale = 18; // SVG 크기를 키우는 비율
  const logoWidth = 22 * logoScale;
  const logoHeight = 17 * logoScale;
  const logoX = centerX - logoWidth / 2;
  const logoY = centerY - logoHeight / 2;

  // 로고 경로 그리기 (SVG 경로를 Canvas로 변환)
  ctx.save();
  ctx.translate(logoX, logoY);
  ctx.scale(logoScale, logoScale);

  // 로고의 메인 경로
  // 그라데이션 생성 (시크한 메탈릭 느낌)
  const logoGradient = ctx.createLinearGradient(0, 0, 22, 17);
  logoGradient.addColorStop(0, "#FFFFFF"); // 시작
  logoGradient.addColorStop(0.3, "#DDDDDD"); // 중간
  logoGradient.addColorStop(0.7, "#EEEEEE"); // 중간
  logoGradient.addColorStop(1, "#FFFFFF"); // 끝

  ctx.fillStyle = logoGradient;

  // 첫 번째 경로
  ctx.beginPath();
  ctx.moveTo(21.6522, 4.73203);
  ctx.lineTo(20.7159, 7.63195);
  ctx.lineTo(20.6897, 7.63195);
  ctx.lineTo(16.1861, 10.5319);
  ctx.lineTo(10.2393, 10.5319);
  ctx.lineTo(11.2018, 7.63195);
  ctx.lineTo(14.9949, 7.63195);
  ctx.lineTo(15.9575, 4.73203);
  ctx.lineTo(10.2365, 4.73203);
  ctx.lineTo(9.30014, 7.63195);
  ctx.lineTo(8.33758, 10.5319);
  ctx.lineTo(7.40126, 13.4318);
  ctx.lineTo(6.4387, 16.3317);
  ctx.lineTo(0.744873, 16.3317);
  ctx.lineTo(1.70743, 13.4318);
  ctx.lineTo(2.64375, 10.5319);
  ctx.lineTo(3.60631, 7.63195);
  ctx.lineTo(4.54263, 4.73203);
  ctx.lineTo(5.98694, 0.333496);
  ctx.lineTo(17.4027, 0.333496);
  ctx.lineTo(21.6522, 4.73203);
  ctx.closePath();

  ctx.moveTo(13.097, 13.4327);
  ctx.lineTo(14.1767, 10.5328);
  ctx.lineTo(19.7543, 10.5328);
  ctx.lineTo(19.7543, 10.558);
  ctx.lineTo(18.818, 13.4336);
  ctx.lineTo(17.8554, 16.3335);
  ctx.lineTo(12.1606, 16.3335);
  ctx.lineTo(13.097, 13.4336);
  ctx.lineTo(13.097, 13.4327);
  ctx.closePath();
  ctx.fill();

  // 두 번째 경로
  ctx.beginPath();
  ctx.moveTo(10.4791, 14.6875);
  ctx.lineTo(10.7406, 13.8999);
  ctx.lineTo(12.2871, 13.8999);
  ctx.lineTo(12.2871, 13.9071);
  ctx.lineTo(12.0331, 14.6875);
  ctx.lineTo(11.7716, 15.4751);
  ctx.lineTo(10.2251, 15.4751);
  ctx.lineTo(10.4791, 14.6875);
  ctx.closePath();
  ctx.fill();
  ctx.restore();

  // 미묘한 하이라이트 효과
  ctx.save();
  ctx.globalCompositeOperation = "overlay";
  const highlightGradient = ctx.createLinearGradient(
    centerX - 200,
    centerY - 200,
    centerX + 200,
    centerY + 200,
  );
  highlightGradient.addColorStop(0, "#FFFFFF10");
  highlightGradient.addColorStop(0.5, "#FFFFFF00");
  highlightGradient.addColorStop(1, "#FFFFFF10");

  ctx.fillStyle = highlightGradient;
  ctx.beginPath();
  ctx.arc(centerX, centerY, 200, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();

  // 매우 미묘한 빛나는 효과 (아주 미세하게)
  ctx.save();
  ctx.beginPath();
  ctx.arc(centerX, centerY, 210, 0, Math.PI * 2);
  const glowGradient = ctx.createRadialGradient(
    centerX,
    centerY,
    190,
    centerX,
    centerY,
    210,
  );
  glowGradient.addColorStop(0, "#FFFFFF08"); // 매우 투명한 흰색
  glowGradient.addColorStop(1, "#FFFFFF00");
  ctx.fillStyle = glowGradient;
  ctx.fill();
  ctx.restore();

  // 로고 아래에 최소한의 문구 (시크한 느낌으로)
  ctx.font = "40px Pixel-Regular";
  ctx.fillStyle = "#888888"; // 연한 회색
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";
  ctx.fillText("RABBIT", centerX, centerY + 250);

  // 발행연도 (미니멀하게)
  ctx.font = "24px Pretendard-Regular";
  ctx.fillStyle = "#444444"; // 더 어두운 회색
  ctx.fillText("2025", centerX, centerY + 300);

  return new CanvasTexture(canvas);
};

export default createSleekGridTexture;
