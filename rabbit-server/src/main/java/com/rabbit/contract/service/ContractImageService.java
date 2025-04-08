package com.rabbit.contract.service;

import com.rabbit.contract.domain.entity.Contract;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * 계약서 이미지 생성 서비스
 */
@Slf4j
@Service
public class ContractImageService {

    /**
     * 계약서 기반 NFT 이미지 생성
     *
     * @param contract 계약 엔티티
     * @return 생성된 이미지 바이트 배열
     */
    public byte[] generateContractImage(Contract contract) {
        try {
            int width = 1024;
            int height = 1024;

            // 계약 ID를 시드로 사용하여 이미지 생성이 결정적이게 함
            Random rand = new Random(contract.getContractId().hashCode());

            // 1. 배경 생성: 계약 정보 기반 랜덤 도트 배경
            BufferedImage background = generateRandomDotBackground(width, height, rand);

            // 2. 파츠 이미지 로드
            BufferedImage base = loadImage("/nft/base.png");
            BufferedImage hat = loadImage("/nft/hat.png");
            BufferedImage jacket = loadImage("/nft/jacket.png");
            BufferedImage pants = loadImage("/nft/pants.png");
            BufferedImage shoes = loadImage("/nft/shoes.png");

            // 3. 각 파츠에 계약 정보 기반 색상 적용
            // 대출 금액, 이자율, 기간 등에 따라 색상을 결정적으로 변경
            Color hatColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            Color jacketColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            Color pantsColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            Color shoesColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

            BufferedImage coloredHat = colorizePart(hat, hatColor);
            BufferedImage coloredJacket = colorizePart(jacket, jacketColor);
            BufferedImage coloredPants = colorizePart(pants, pantsColor);
            BufferedImage coloredShoes = colorizePart(shoes, shoesColor);

            // 4. 최종 합성
            int X_ = -2;
            int Y_ = -5;
            Graphics2D g = background.createGraphics();
            g.drawImage(base, 340 + X_, 45 + Y_, null);
            g.drawImage(coloredPants, 434 + X_, 746 + Y_, null);
            g.drawImage(coloredJacket, 377 + X_, 538 + Y_, null);
            g.drawImage(coloredShoes, 434 + X_, 898 + Y_, null);
            g.drawImage(coloredHat, 359 + X_, 216 + Y_, null);

            // 5. 계약 정보 텍스트 추가 (선택적)
            addContractInfo(g, contract);

            g.dispose();

            // 6. 이미지를 바이트 배열로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(background, "png", baos);

            log.info("[이미지 생성] 계약 ID: {} 기반 NFT 이미지 생성 완료", contract.getContractId());
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("[이미지 생성 실패] 계약 ID: {}, 오류: {}", contract.getContractId(), e.getMessage(), e);
            throw new RuntimeException("NFT 이미지 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 리소스에서 이미지 로드
     */
    private BufferedImage loadImage(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        return ImageIO.read(resource.getInputStream());
    }

    /**
     * 랜덤 도트 배경 생성
     */
    private BufferedImage generateRandomDotBackground(int width, int height, Random rand) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 계약 정보에 기반한 배경 색상
        int r = rand.nextInt(256);
        int gColor = rand.nextInt(256);
        int b = rand.nextInt(256);
        Color backgroundColor = new Color(r, gColor, b);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);

        // 도트 효과
        Color[] dotColors = {
                new Color(80, 80, 120, 220),   // 어두운 블루
                new Color(100, 60, 120, 220),   // 어두운 퍼플
                new Color(60, 80, 90, 220),     // 다크 틸
                new Color(90, 90, 90, 220)      // 어두운 그레이
        };

        int numberOfDots = 500;
        for (int i = 0; i < numberOfDots; i++) {
            int diameter = rand.nextInt(7) + 4;
            int x = rand.nextInt(width - diameter);
            int y = rand.nextInt(height - diameter);
            Color dotColor = dotColors[rand.nextInt(dotColors.length)];
            g.setColor(dotColor);
            g.fillOval(x, y, diameter, diameter);
        }

        g.dispose();
        return image;
    }

    /**
     * 파츠 색상 변경
     */
    private BufferedImage colorizePart(BufferedImage partImage, Color newColor) {
        int width = partImage.getWidth();
        int height = partImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = partImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha == 0) {
                    result.setRGB(x, y, pixel);
                } else {
                    int rgb = (alpha << 24) | (newColor.getRed() << 16) | (newColor.getGreen() << 8) | newColor.getBlue();
                    result.setRGB(x, y, rgb);
                }
            }
        }
        return result;
    }

    /**
     * 계약 정보 텍스트 추가 (선택적)
     */
    private void addContractInfo(Graphics2D g, Contract contract) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 이미지 하단에 계약 정보 표시 (예: 금액이나 계약자명 첫글자 등)
        String info = "Contract #" + contract.getContractId();
        int x = 50;
        int y = 980;

        // 텍스트 윤곽선 효과 (가독성 향상)
        g.setColor(Color.BLACK);
        g.drawString(info, x-1, y-1);
        g.drawString(info, x+1, y-1);
        g.drawString(info, x-1, y+1);
        g.drawString(info, x+1, y+1);

        // 실제 텍스트
        g.setColor(Color.WHITE);
        g.drawString(info, x, y);
    }
}