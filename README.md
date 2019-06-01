# SpringBoot_ChatService
🌿💦 실시간 채팅서비스를 위한 SrpingBoot REST API  

<br/>

### Environment
- Spring Boot 2.1.5.RELEASE
- Java 1.8
- spring-boot-starter-websocket
- spring-kafka
- guava


### Tech Description
- [WebSocket](https://hyeooona825.tistory.com/89)
- [Kafka](https://taetaetae.github.io/2017/11/02/what-is-kafka/)
- [Guava](https://blog.outsider.ne.kr/710)


  

## Review



#### WebSocketConfig.java

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chatting").setAllowedOrigins("*").withSockJS();
    }
}

```

+ configureMessageBroker : Message Broker에 대한 설정을 진행하는 메소드이다.
  +  enableSimpleBroke :  파라미터로 넣어 준 경로로 Simple Broker를 등록한다. Simple Broker는 해당 경로를  Subscribe하는 클라이언트에게 메시지를 전달하는 작업을 수행한다.
  + setApplicationDestinationPrefixes : 클라이언트에서의 send 요청을 처리, 만약 topic/hello 토픽에 대해 구독 신청시, 실제 경로는 /app/topic/hello 가 된다.
+ registerStompEndpoints : handshake와 통신을 담당할 **endpoint**를 지정한다.또한 setAllowedOrigins(*)를 통해 웹소켓 통신이 소켓 객체를 생성한 시점이 아니더라도 통신이 가능 할 수 있게 해준다. 예를 들어 <http://localhost:8080/stomp> 로 웹소켓 객체를 생성했는데 (설정시점) [http://localhost](http://localhost/) 로 웹소켓 통신을 시도하면 (요청시점) connect 이 이뤄지지 않는다. 정확히 포트번호까지 일치해야 한다. 따라서 설정된 도메인 외에 연결을 허용할 도메인을 지정해줌으로서 문제를 해결한다. 단 소켓 객체를 연결한 도메인에서만 채팅이 이루어진다면 필요하지 않다.



#### Reciver.java

```java
@Service
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(id = "main-listener", topics = "kafka-chatting")
    public void receive(ChattingMessage message) throws Exception{
        LOGGER.info("message='{}'", message);
        HashMap<String, String> msg = new HashMap<>();
        msg.put("timestamp", Long.toString(message.getTimeStamp()));
        msg.put("message", message.getMessage());
        msg.put("author", message.getUser());

        ObjectMapper mapper = new ObjectMapper(); // 왜 쓰는거지?
        String json = mapper.writeValueAsString(msg);

        this.template.convertAndSend("/topic/public", json);
    }
}

```

+ ChattingMessage를 HashMap으로 바꾸고, 이를 Jackson 라이브러리에서 제공하는 Object mapper를 통해 JSON 형태로 변환한다. 이를 SimpMessagingTemplated을 통해 /topic/public으로 메시지를 보낸다.



#### ChattingHistoryDAO

```java
@Component
public class ChattingHistoryDAO {

    private final Cache<UUID, ChattingMessage> chatHistoryCache = CacheBuilder
            .newBuilder().maximumSize(20).expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public void save(ChattingMessage chatObj){
        this.chatHistoryCache.put(UUID.randomUUID(), chatObj);
    }

    public List<ChattingMessage> get(){
        return chatHistoryCache.asMap().values().stream()
                .sorted(Comparator.comparing(ChattingMessage::getTimeStamp))
                .collect(Collectors.toList());
    }
}
```

+ 데이터베이스 대신 guava에서 제공하는 캐시를 사용하여 메시지를 저장한다. 

#### 

#### 

