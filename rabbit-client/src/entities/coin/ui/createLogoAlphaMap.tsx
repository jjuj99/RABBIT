import { CanvasTexture } from "three";

const createLogoAlphaMap = () => {
  const canvas = document.createElement("canvas");
  canvas.width = 1024;
  canvas.height = 1024;
  const ctx = canvas.getContext("2d");

  if (!ctx) return null;

  // 투명 배경
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const centerX = canvas.width / 2;
  const centerY = canvas.height / 2;

  // 로고 스케일 및 위치 계산
  const logoScale = 18; // SVG 크기를 키우는 비율
  const logoWidth = 22 * logoScale;
  const logoHeight = 17 * logoScale;
  const logoX = centerX - logoWidth / 2;
  const logoY = centerY - logoHeight / 2;

  // 로고만 그리기 (텍스트 제외)
  ctx.save();
  ctx.translate(logoX, logoY);
  ctx.scale(logoScale, logoScale);

  // 로고 채우기는 단색으로 (높이맵용)
  ctx.fillStyle = "#FFFFFF";

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

  return new CanvasTexture(canvas);
};

export default createLogoAlphaMap;
