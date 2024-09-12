package kr.co.ureca.sockettest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration @EnableWebSocketMessageBroker @RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/coupong").setAllowedOrigins("http://localhost:3000")
                .withSockJS();
        // 연결 주소 = ws://localhost:8080/coupong
        // 해당 통신이 웹 소켓 통신 중 stomp 통신인 것을 확인하고 연결 => 모든 출처에서의 연결을 허용하도록 설정
        // withSockJS를 통해 웹 소켓을 지원하지 않는 브라우저에 대해 웹 소켓을 대체
    }
    @Override 	// 한 클라이언트에서 다른 클라이언트로 메시지를 라우팅하는데 사용될 메시지 브로커
    public void configureMessageBroker(MessageBrokerRegistry registry) { 
        registry.enableSimpleBroker("/sub"); // /sub으로 시작되는 요청을 구독한 모든 사용자들에게 메시지를 broadcast - 구독 요청 url
        registry.setApplicationDestinationPrefixes("/pub"); // pub으로 시작되는 메시지는 message-handling methods로 라우팅 = 서버 측에서 처리할 메시지 경로 - 송신 요청 url
    }

}