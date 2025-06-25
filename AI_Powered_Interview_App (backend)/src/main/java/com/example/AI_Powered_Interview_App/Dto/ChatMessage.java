package com.example.AI_Powered_Interview_App.Dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;  // "user" or "assistant"
    private String content;

    public ChatMessage() {}
    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

}

