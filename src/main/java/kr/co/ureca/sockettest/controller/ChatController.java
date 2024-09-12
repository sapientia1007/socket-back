package kr.co.ureca.sockettest.controller;

import kr.co.ureca.sockettest.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/* 채팅 리스트를 반환하는 API/메시지 송신 시 처리하는 메서드*/

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    HashSet<String> userSessions = new HashSet<>();

    /*
     * MessageMapping : 클라이언트가 보낸 메시지를 어떤 메서드로 라우팅할지 결정
     */
    @MessageMapping(value = "/enter")
    public void enter(ChatMessage message, SimpMessageHeaderAccessor accessor) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        String sessionId = accessor.getSessionId();
//        System.out.println("enter : " + sessionId);

        userSessions.add(sessionId);
//        System.out.println(userSessions);
//        message.setId(message.getId());
        message.setMessage(message.getName() + "님이 입장하였습니다");
        message.setCreatedDate(formattedDate);

        ChatMessage chatMessage = new ChatMessage(message.getName(),message.getMessage(), message.getCreatedDate());
        template.convertAndSend("/sub/coupong", chatMessage);
        // "/sub/coupong"으로 들어온 객체 message를 도착지점을 구독하고 있는 사용자에게 메시지 전달
    }

    @MessageMapping(value = "/messages")
    public void sendMessage(ChatMessage message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
//        System.out.println(userSessions);
//        message.setId(message.getId());
        message.setCreatedDate(formattedDate);
        template.convertAndSend("/sub/coupong", message);
    }

    @MessageMapping(value = "/exit")
    public void exit(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        String sessionId = accessor.getSessionId();
        System.out.println("exit : " + sessionId);

        userSessions.remove(sessionId);

        System.out.println("after exit" +userSessions);

//        message.setId(message.getId());
        message.setMessage(message.getName() + "님이 퇴장하였습니다");
        message.setCreatedDate(formattedDate);

        if (!userSessions.contains(sessionId)) {
            ChatMessage chatMessage = new ChatMessage(message.getName(), message.getMessage(), message.getCreatedDate());
            // 모든 사용자에게 메시지 전송
            template.convertAndSend("/sub/coupong", chatMessage);
            System.out.println("나간 사용자 : " + message.getName());
        }
        System.out.println("Current sessions: " + userSessions);
    }
}
