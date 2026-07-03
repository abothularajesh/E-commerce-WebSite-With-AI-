package com.telusko.SpringEcom.controller;

import com.telusko.SpringEcom.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @GetMapping("{conversationId}/ask")
    public ResponseEntity<String> askBot(@RequestParam String conversationId,@RequestParam String message){

        String response = chatBotService.getBotResponse(conversationId,message);
        return ResponseEntity.ok(response);
    }
}
