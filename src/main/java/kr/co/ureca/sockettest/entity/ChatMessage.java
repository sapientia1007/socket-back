package kr.co.ureca.sockettest.entity;

import lombok.*;


/* 반환되는 데이터 타입 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    private String name;
    private String message;
    private String createdDate;
}
