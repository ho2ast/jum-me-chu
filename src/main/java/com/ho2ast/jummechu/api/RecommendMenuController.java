package com.ho2ast.jummechu.api;

import com.google.gson.Gson;
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

        int pageNum = (int) (Math.random() * 3 + 1);
        String page = String.valueOf(pageNum);

        WebClient build = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KAKAO API KEY")
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
        Map map = (Map) documents.get(listNum);

        Map<String, Object> result = new HashMap<>();
        result.put("body", "[["+map.get("place_name")+"]]("+map.get("place_url")+")");
        result.put("connectColor", "#FAC11B");

        ArrayList<Object> list = new ArrayList<>();

        Map<String, Object> placeName = new HashMap<>();
        placeName.put("title", "식당");
        placeName.put("description", map.get("place_name"));
        list.add(placeName);

        Map<String, Object> category = new HashMap<>();
        category.put("title", "분류");
        category.put("description", map.get("category_name"));
        list.add(category);

        Map<String, Object> location = new HashMap<>();
        location.put("title", "위치");
        location.put("description", map.get("place_url"));
        list.add(location);

        result.put("connectInfo", list);

        String body = new Gson().toJson(result);

        WebClient.builder()
                .baseUrl("Webhook 수신 URL")
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
