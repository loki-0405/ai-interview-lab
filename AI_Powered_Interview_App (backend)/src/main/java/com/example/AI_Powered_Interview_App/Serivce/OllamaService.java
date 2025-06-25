package com.example.AI_Powered_Interview_App.Serivce;
import com.example.AI_Powered_Interview_App.Dto.ChatMessage;
import com.example.AI_Powered_Interview_App.Dto.OllamaMessage;
import com.example.AI_Powered_Interview_App.Dto.OllamaRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL = "http://localhost:11434/api/chat";

    public String chatWithModel(List<ChatMessage> chatHistory) {
        OllamaRequest request = new OllamaRequest();
        request.setModel("deepseek-r1:1.5b");
        request.setStream(false);

        List<OllamaMessage> messages = chatHistory.stream()
                .map(m -> new OllamaMessage(m.getSender(), m.getContent()))
                .collect(Collectors.toList());
        request.setMessages(messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);
        Map<String, Object> msg = (Map<String, Object>) response.getBody().get("message");

        String rawResponse = msg.get("content").toString();

        String noThinkSection = rawResponse.replaceAll("(?s)<think>.*?</think>", "").trim();


     return noThinkSection;
    }
}
