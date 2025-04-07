import { CanvasTexture } from "three";

// 시크하고 격자 패턴이 있는 코인 뒷면 텍스처 생성 함수
const createSleekGridBackTexture = () => {
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

  // 동적인 선 패턴 추가 (방향 달리하여 앞면과 다른 느낌)
  ctx.strokeStyle = "#222222";
  ctx.lineWidth = 2;

  // 방사형 패턴
  for (let i = 0; i < 12; i++) {
    const angle = (i / 12) * Math.PI * 2;
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(
      centerX + Math.cos(angle) * 500,
      centerY + Math.sin(angle) * 500,
    );
    ctx.stroke();
  }

  // 대각선 패턴 추가
  ctx.strokeStyle = "#2A2A2A";
  ctx.lineWidth = 1;

  for (let i = -10; i <= 10; i += 2) {
    // 왼쪽 아래에서 오른쪽 위로
    ctx.beginPath();
    ctx.moveTo(0, centerY + i * 50);
    ctx.lineTo(canvas.width, centerY - i * 50);
    ctx.stroke();

    // 왼쪽 위에서 오른쪽 아래로
    ctx.beginPath();
    ctx.moveTo(0, centerY - i * 50);
    ctx.lineTo(canvas.width, centerY + i * 50);
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

  // 블록체인 관련 미니멀 디자인
  // 흐름도 같은 느낌의 블록 요소들
  ctx.strokeStyle = "#333333";
  ctx.lineWidth = 2;

  // 여러 개의 사각형 블록 연결
  const blocks = [
    { x: centerX - 150, y: centerY - 50, w: 60, h: 30 },
    { x: centerX - 50, y: centerY - 50, w: 60, h: 30 },
    { x: centerX + 50, y: centerY - 50, w: 60, h: 30 },
    { x: centerX - 150, y: centerY + 50, w: 60, h: 30 },
    { x: centerX - 50, y: centerY + 50, w: 60, h: 30 },
    { x: centerX + 50, y: centerY + 50, w: 60, h: 30 },
    { x: centerX + 150, y: centerY, w: 60, h: 30 },
  ];

  // 블록들 그리기
  blocks.forEach((block) => {
    ctx.fillStyle = "#181818";
    ctx.fillRect(block.x, block.y, block.w, block.h);
    ctx.strokeRect(block.x, block.y, block.w, block.h);
  });

  // 블록 연결하는 선
  ctx.beginPath();
  ctx.moveTo(blocks[0].x + blocks[0].w, blocks[0].y + blocks[0].h / 2);
  ctx.lineTo(blocks[1].x, blocks[1].y + blocks[1].h / 2);

  ctx.moveTo(blocks[1].x + blocks[1].w, blocks[1].y + blocks[1].h / 2);
  ctx.lineTo(blocks[2].x, blocks[2].y + blocks[2].h / 2);

  ctx.moveTo(blocks[2].x + blocks[2].w, blocks[2].y + blocks[2].h / 2);
  ctx.lineTo(blocks[6].x, blocks[6].y + blocks[6].h / 2);

  ctx.moveTo(blocks[6].x + blocks[6].w / 2, blocks[6].y);
  ctx.lineTo(blocks[6].x + blocks[6].w / 2, blocks[6].y - 20);
  ctx.lineTo(blocks[0].x + blocks[0].w / 2, blocks[0].y - 20);
  ctx.lineTo(blocks[0].x + blocks[0].w / 2, blocks[0].y);

  ctx.moveTo(blocks[3].x + blocks[3].w, blocks[3].y + blocks[3].h / 2);
  ctx.lineTo(blocks[4].x, blocks[4].y + blocks[4].h / 2);

  ctx.moveTo(blocks[4].x + blocks[4].w, blocks[4].y + blocks[4].h / 2);
  ctx.lineTo(blocks[5].x, blocks[5].y + blocks[5].h / 2);

  ctx.moveTo(blocks[5].x + blocks[5].w, blocks[5].y + blocks[5].h / 2);
  ctx.lineTo(blocks[6].x, blocks[6].y + blocks[6].h / 2);

  ctx.moveTo(blocks[0].x + blocks[0].w / 2, blocks[0].y + blocks[0].h);
  ctx.lineTo(blocks[0].x + blocks[0].w / 2, blocks[3].y);

  ctx.moveTo(blocks[1].x + blocks[1].w / 2, blocks[1].y + blocks[1].h);
  ctx.lineTo(blocks[1].x + blocks[1].w / 2, blocks[4].y);

  ctx.moveTo(blocks[2].x + blocks[2].w / 2, blocks[2].y + blocks[2].h);
  ctx.lineTo(blocks[2].x + blocks[2].w / 2, blocks[5].y);

  ctx.stroke();

  // 미묘한 해시 문자열 (매우 옅게)
  ctx.font = "12px monospace";
  ctx.fillStyle = "#333333";
  ctx.textAlign = "center";

  // 원주를 따라 텍스트 배치
  const hashText =
    "0x1A2B3C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A0B1C2D3E4F5A6B7C8D9E0F";
  const chars = hashText.split("");
  const radius = 420;

  ctx.save();
  ctx.translate(centerX, centerY);

  chars.forEach((char, i) => {
    const angle = (i / chars.length) * Math.PI * 2 - Math.PI / 2;
    ctx.save();
    ctx.rotate(angle);
    ctx.translate(0, -radius);
    ctx.rotate(Math.PI / 2);
    ctx.fillText(char, 0, 0);
    ctx.restore();
  });

  ctx.restore();

  // 심플한 블록체인 관련 텍스트
  ctx.font = "16px Pixel-Regular";
  ctx.fillStyle = "#555555";
  ctx.textAlign = "center";
  ctx.fillText("DISTRIBUTED LEDGER", centerX, centerY + 240);

  // 이스터 에그: 친구들의 이름을 영어로 변환하여 테두리를 따라 배치
  const names = [
    "PARK SOOYANG",
    "MOON JONGHA",
    "SEO JUWON",
    "JEONG YUJIN",
    "LEE GAHYEON",
    "KIM YOUMIN",
  ];

  // 원형으로 텍스트 배치
  ctx.font = "16px Pixel-Regular";
  ctx.fillStyle = "#444444";
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";

  const nameRadius = 420; // 테두리에서 더 안쪽으로 (450 -> 420)

  names.forEach((name, index) => {
    const angle = (index / names.length) * Math.PI * 2 - Math.PI / 2;
    ctx.save();
    ctx.translate(centerX, centerY);
    ctx.rotate(angle);
    ctx.translate(0, -nameRadius);
    ctx.rotate(Math.PI / 2);
    ctx.fillText(name, 0, 0);
    ctx.restore();
  });

  return new CanvasTexture(canvas);
};

export default createSleekGridBackTexture;
