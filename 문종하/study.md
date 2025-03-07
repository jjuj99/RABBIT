# 📆 목요일 (2025-03-06)

## 🔍 제목: RESTful API에서 페이지네이션과 검색 구현 패턴 비교 분석

## 💡 배운 내용
오늘은 JPA 프로젝트에서 페이지네이션과 검색 기능을 구현하는 여러 방법에 대해 학습했습니다. 크게 다음과 같은 내용을 배웠습니다:

1. **페이지네이션 구현 방법**:
   - Spring Data JPA의 `@PageableDefault`를 사용한 간결한 방식
   - 커스텀 `PageRequestDTO`를 활용한 유연한 방식

2. **검색 구현 방법**:
   - JPA의 Example 패턴을 활용한 검색 기능 구현
   - 추상 클래스를 활용한 범용적인 검색 DTO 설계

3. **REST API 설계 패턴**:
   - GET 요청 + 쿼리 파라미터: RESTful 원칙에 충실한 방식
   - POST 요청 + 요청 본문: 복잡한 검색 조건에 적합한 방식
   
4. **Swagger UI와 관련된 문제점**:
   - GET 요청에 `@RequestBody`를 사용할 때 발생하는 이슈 및 해결 방법


## 🔧 실제 적용 방법
현재 개발 중인 프로젝트에는 다음과 같이 적용할 계획입니다:

1. **단순 조회 API**: `@PageableDefault`와 쿼리 파라미터를 사용
   ```java
   @GetMapping
   public ResponseEntity<Page<ItemDTO>> getItems(
       @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
       Pageable pageable) {
       return ResponseEntity.ok(itemService.findAll(pageable));
   }
   ```

2. **복잡한 검색 API**: 전용 검색 엔드포인트와 POST 요청 사용
   ```java
   @PostMapping("/search")
   public ResponseEntity<Page<ItemDTO>> searchItems(
       @Valid @RequestBody ItemSearchDTO searchDTO) {
       return ResponseEntity.ok(itemService.searchItems(searchDTO));
   }
   ```

3. **하이브리드 접근법**: 중간 복잡도의 검색에는 `@ModelAttribute`와 GET 요청 사용
   ```java
   @GetMapping("/filter")
   public ResponseEntity<Page<ItemDTO>> filterItems(
       @ModelAttribute @Valid ItemFilterDTO filterDTO,
       @PageableDefault(page = 0, size = 10) Pageable pageable) {
       return ResponseEntity.ok(itemService.filterItems(filterDTO, pageable));
   }
   ```

## 🔎 추가 학습이 필요한 부분
1. **JPA Specification**: 더 복잡한 동적 쿼리를 위한 Specification 패턴 심화 학습
2. **QueryDSL**: 타입 안전한 쿼리 작성을 위한 QueryDSL 학습
3. **HATEOAS**: 하이퍼미디어를 포함한 RESTful API 구현 방법
4. **성능 최적화**: 대용량 데이터에서 페이지네이션 성능 최적화 기법

## 💭 느낀 점
오늘 학습을 통해 API 설계에는 정답이 없다는 것을 다시 한번 깨달았습니다. RESTful 원칙을 완벽하게, 엄격하게 따르는 것과 실용적인 접근법 사이에서 균형을 찾는 것이 중요하다고 생각합니다. 특히 복잡한 비즈니스 요구사항을 표현할 때는 때로는 순수 RESTful 원칙에서 벗어나는 것이 더 효율적일 수 있습니다.

또한 Spring Data JPA가 제공하는 풍부한 기능들을 제대로 활용하면 반복적인 코드를 크게 줄일 수 있다는 점도 인상적이었습니다. 앞으로 공부할 QueryDSL과 함께 활용하면 더 강력한 검색 기능을 구현할 수 있을 것으로 기대됩니다.

---

# 📆 수요일 (2025-03-05)

## 🔍 제목
**블록체인의 탈중앙화 신뢰 메커니즘과 실생활 응용**

## 💡 배운 내용
탈중앙화된 신뢰 구축 메커니즘 학습

주요 학습 포인트:
1. **합의 알고리즘의 다양성**: 작업 증명(PoW), 지분 증명(PoS), 권한 증명(PoA) 등 다양한 합의 알고리즘이 각각 다른 방식으로 신뢰를 구축한다는 점을 이해했습니다. 특히 각 알고리즘의 장단점과 적합한 사용 사례가 있다는 것이 흥미로웠습니다.

2. **데이터 불변성 원리**: 해시 체인과 머클 트리 구조를 통해 블록체인이 어떻게 데이터의 무결성을 보장하는지 학습했습니다. 한 번 기록된 정보가 변경 불가능한 이유가 단순히 규칙이 아니라 수학적, 암호학적 원리에 기반한다는 점이 매우 흥미로웠습니다.

3. **오라클 문제와 해결책**: 블록체인이 외부 세계의 정보를 신뢰할 수 있게 가져오는 과정의 어려움(오라클 문제)과 이를 해결하기 위한 분산형 오라클, 다중 소스 검증 등의 접근법을 배웠습니다.

4. **실물 자산 연계 방법론**: 토큰화, 공급망 관리, 디지털 아이덴티티 등을 통해 실제 세계의 자산과 정보를 블록체인에 신뢰성 있게 연결하는 방법에 대해 이해했습니다.

## 🔎 추가 학습이 필요한 부분
1. **영지식 증명(Zero-Knowledge Proofs)**: 프라이버시 보호와 데이터 검증을 동시에 달성할 수 있는 이 기술에 대해 더 자세히 공부해야 합니다.

2. **스마트 계약 보안**: 스마트 계약의 취약점과 이를 방지하기 위한 보안 관행에 대해 더 깊이 학습할 필요가 있습니다.

3. **규제 및 법적 프레임워크**: 블록체인 기술의 실제 적용에 영향을 미치는 다양한 국가의 규제 환경과 법적 고려사항에 대한 이해를 넓혀야 합니다.

---

# 📆 화요일 (2025-03-04)
개인 사정으로 인한 결석