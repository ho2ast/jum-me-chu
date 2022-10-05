# 점심 식당 랜덤 뽑기 Webhook API
- 점심용 식당을 랜덤으로 뽑아 주는 잔디 Webhook용 API 입니다.
- 잔디에서 /점메추 입력시 랜덤으로 식당을 선정해줍니다.
- 회사 반경 1000m 이내의 식당이며 가격등은 제공하지 않습니다.
```java
build.get()
    .uri(uri -> {
        return uri.path("/v2/local/search/category.json")
                .queryParam("y", "37.52702497417115") // 회사 좌표
                .queryParam("x", "126.92782280580694") // 회사 좌표
                .queryParam("radius", "1000") // 반경 1000m 이내
                .queryParam("category_group_code", "FD6") // 카테고리 - 음식점
                .queryParam("page", page) // 페이지 번호
                .build();
    })
    .retrieve()
    .bodyToMono(Map.class)
    .block();
```
- 재미를 위해서 무지성 코딩으로 대충 만든 API입니다.