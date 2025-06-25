package com.example.AI_Powered_Interview_App.Controller;

import com.example.AI_Powered_Interview_App.Dto.ChatMessage;
import com.example.AI_Powered_Interview_App.Serivce.OllamaService;
import com.example.AI_Powered_Interview_App.Serivce.PrepService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prep")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PrepController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private PrepService prepService;

    // Store history per session
    private final Map<String, List<ChatMessage>> sessionChats = new HashMap<>();

    // 🟢 Step 1: Upload resume + job info ONCE
    @PostMapping("/start")
    public ResponseEntity<?> startChat(@RequestParam("file") MultipartFile file,
                                       @RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       HttpSession session) {

        String sessionId = session.getId();
        String resumeText = prepService.LoadData(file);

        String systemMsg = "This is my resume:\n" + resumeText +
                "\nJob Title:\n" + title +
                "\nJob Description:\n" + description;

        List<ChatMessage> chatHistory = new ArrayList<>();
        chatHistory.add(new ChatMessage("system", systemMsg));

        sessionChats.put(sessionId, chatHistory);
        return ResponseEntity.ok("Session started with resume and job description.");
    }

    // 🟢 Step 2: Continue chatting (without file)
    @PostMapping
    public ResponseEntity<List<ChatMessage>> continueChat(@RequestBody String message, HttpSession session) {
           String sessionId = session.getId();

        if (!sessionChats.containsKey(sessionId)) {
            return ResponseEntity.badRequest().body(List.of(new ChatMessage("system", "No active session. Please upload resume first.")));
        }

        List<ChatMessage> chatHistory = sessionChats.get(sessionId);
        chatHistory.add(new ChatMessage("user", message));

        String response = ollamaService.chatWithModel(chatHistory);
        chatHistory.add(new ChatMessage("assistant", response));

        return ResponseEntity.ok(chatHistory);
    }

    // 🔴 Optional: End chat session
    @DeleteMapping("/end")
    public ResponseEntity<String> endChat(HttpSession session) {
        sessionChats.remove(session.getId());
        return ResponseEntity.ok("Chat session ended.");
    }

    // 🟡 Optional: Get chat history
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(HttpSession session) {
        return ResponseEntity.ok(sessionChats.getOrDefault(session.getId(), new ArrayList<>()));
    }
}
