# 🐰 RABBIT - NFT 기반 차용증 관리 서비스

[![React](https://img.shields.io/badge/React-v19-blue)](https://react.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Solidity](https://img.shields.io/badge/Solidity-v0.8.28-black)](https://soliditylang.org/)
[![Web3.js](https://img.shields.io/badge/Web3.js-v1.4.0-orange)](https://web3js.readthedocs.io/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-v4-06B6D4)](https://tailwindcss.com/)

<div align="center">
  <img src="exec/img/스크린샷%202025-04-10%20144140.png" alt="RABBIT 메인화면" width="800"/>
</div>

## 📌 목차

- [서비스 소개](#-서비스-소개)
- [기술 스택](#-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [주요 기능](#-주요-기능)
- [설치 및 실행 방법](#-설치-및-실행-방법)
- [팀 소개](#-팀-소개)

## 🔍 서비스 소개

**RABBIT**은 블록체인 기술을 활용한 신개념 차용증 관리 서비스입니다.

전통적인 종이 차용증의 문제점(분실 위험, 위조 가능성, 관리의 어려움)을 해결하고, NFT 기술을 통해 차용증을 디지털 자산으로 관리할 수 있습니다. 이를 통해 사용자는 자신의 채권과 채무를 투명하게 관리하고, 필요시 경매를 통해 채권을 양도할 수 있습니다.

주요 특징:

- **블록체인 기반 차용증**: 변조 불가능한 스마트 계약으로 차용증 발행
- **NFT 차용증**: 각 차용증은 유일한 NFT로 발행되어 소유권 명확
- **채권 양도 시스템**: 경매를 통한 채권 NFT 거래 지원
- **투명한 거래 이력**: 모든 거래 과정이 블록체인에 기록되어 투명하게 관리

## 💻 기술 스택

### 프론트엔드

- **React** v19
- **TailwindCSS** v4
- **TanstackQuery** v5

### 백엔드

- **Spring Boot** 3.3.4
- **PostgreSQL** 17.4
- **RabbitMQ** 3.13.7
- **Redis** 7.2.7
- **Gradle** 8.13

### 블록체인

- **Hardhat** v2.22.19
- **Solidity** v0.8.28 (solc-js)
- **Node.js** v18.20.7
- **npm** 10.8.2
- **Web3.js** v1.4.0

## 🏗 시스템 아키텍처

<div align="center">
  <img src="exec/img/architecture.png" alt="시스템 아키텍처" width="800"/>
  <p><i>* 아키텍처 이미지가 없는 경우, 추후 업데이트 예정</i></p>
</div>

## 🌟 주요 기능

### 1. 차용증 작성 및 관리

차용증을 작성하고 블록체인에 등록할 수 있습니다. 작성된 차용증은 NFT로 발행되어 변조가 불가능합니다.

<div align="center">
  <img src="exec/img/스크린샷%202025-04-10%20150321.png" alt="차용증 작성" width="600"/>
</div>

### 2. 채권/채무 현황 조회

사용자는 자신의 채권과 채무 현황을 한눈에 확인할 수 있습니다.

<div align="center">
  <img src="exec/img/스크린샷%202025-04-10%20150521.png" alt="채권/채무 현황" width="600"/>
</div>

### 3. RAB 코인 입출금

서비스 내 거래를 위한 RAB 코인을 충전하고 출금할 수 있습니다.

<div align="center">
  <img src="exec/img/스크린샷%202025-04-10%20150546.png" alt="RAB 코인 입출금" width="600"/>
</div>

### 4. 차용증 경매

소유한 채권 NFT를 경매에 올리거나, 다른 사용자의 채권을 구매할 수 있습니다.

<div align="center">
  <img src="exec/img/스크린샷%202025-04-10%20150617.png" alt="차용증 경매" width="600"/>
</div>

## 🚀 설치 및 실행 방법

### 사전 요구사항

- Node.js v18.20.7 이상
- Java 17 이상
- PostgreSQL 17.4
- RabbitMQ 3.13.7
- Redis 7.2.7
- MetaMask 지갑

## 👥 팀 소개

**프론트엔드**

- 박수양 (1240291)
- 서주원 (1248831)

**백엔드**

- 문종하 (1247101)
- 정유진 (1241254)
- 김유민 (1243193)

**블록체인**

- 이가현 (1245568)

---

© 2025 RABBIT Team. All Rights Reserved.
