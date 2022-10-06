package com.ho2ast.jummechu.api;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class RecommendMenuController {

    @PostMapping("/getMenu")
    public void getMenu() throws IOException {

        // 페이지 랜덤 선택
        // API에서 실제로는 음식적 목록이 3페이지 이상 조회되나 3페이지 이상 선택될 경우 3페이지로 고정됨.
        int pageNum = (int) (Math.random() * 3 + 1);
        String page = String.valueOf(pageNum);

        WebClient build = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "카카오 API 키")
                .build();

        Map block = build.get()
                .uri(uri -> {
                    return uri.path("/v2/local/search/category.json")
                            .queryParam("y", "37.52702497417115")
                            .queryParam("x", "126.92782280580694")
                            .queryParam("radius", "1000")
                            .queryParam("category_group_code", "FD6")
                            .queryParam("page", page)
                            .build();
                })
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List documents = (List) block.get("documents");

        int listNum = new Random().nextInt(15);
        Collections.shuffle(documents);
        Map map = (Map) documents.get(listNum);

        // webhook return template
        Map<String, Object> result = new HashMap<>();
        result.put("body", "[["+map.get("place_name")+"]]("+map.get("place_url")+")");
        result.put("connectColor", "#FAC11B");

        ArrayList<Object> list = new ArrayList<>();
        Map<String, Object> placeName = Map.of("title", "식당", "description", map.get("place_name"));
        Map<String, Object> category = Map.of("title", "분류","description", map.get("category_name"));
        Map<String, Object> location = Map.of("title", "위치", "description", map.get("place_url"));
        list.add(placeName);
        list.add(category);
        list.add(location);

        result.put("connectInfo", list);

        String body = new Gson().toJson(result);
        WebClient.builder()
                .baseUrl("잔디 웹훅 URL")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.tosslab.jandi-v2+json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build()
                .post()
                .bodyValue(body.getBytes(StandardCharsets.UTF_8))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
