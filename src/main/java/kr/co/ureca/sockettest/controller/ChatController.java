package kr.co.ureca.sockettest.controller;

import kr.co.ureca.sockettest.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/* 채팅 리스트를 반환하는 API/메시지 송신 시 처리하는 메서드*/

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    /*
     * MessageMapping : 클라이언트가 보낸 메시지를 어떤 메서드로 라우팅할지 결정
     */

    public void updateUserCnt() {
        int userCnt = userSessions.size();
        System.out.println(userCnt);
        template.convertAndSend("/sub/usercnt", userCnt);
    }
    @MessageMapping(value = "/enter")
    public void enter(ChatMessage message, SimpMessageHeaderAccessor accessor) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.ENGLISH);
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        String sessionId = accessor.getSessionId();

        userSessions.put(sessionId, message.getName());
        message.setMessage(message.getName() + "님이 입장하였습니다");
        message.setCreatedDate(formattedDate);

        ChatMessage chatMessage = new ChatMessage(message.getName(),message.getMessage(), message.getCreatedDate());
        template.convertAndSend("/sub/coupong", chatMessage);
        // "/sub/coupong"으로 들어온 객체 message를 도착지점을 구독하고 있는 사용자에게 메시지 전달
        updateUserCnt();
    }

    @MessageMapping(value = "/messages")
    public void sendMessage(ChatMessage message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.ENGLISH);
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        message.setCreatedDate(formattedDate);
        template.convertAndSend("/sub/coupong", message);
    }

    @MessageMapping(value = "/exit")
    public void exit(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.ENGLISH);
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        String sessionId = accessor.getSessionId();

        String username = userSessions.remove(sessionId);
        message.setName(username);
        message.setMessage(username+ "님이 퇴장하였습니다");
        message.setCreatedDate(formattedDate);

        if (username != null) {
            ChatMessage chatMessage = new ChatMessage(message.getName(), message.getMessage(), message.getCreatedDate());
            // 모든 사용자에게 메시지 전송
            template.convertAndSend("/sub/coupong", chatMessage);
            System.out.println("나간 사용자 : " + message.getName());
            updateUserCnt();
        }
    }
}
