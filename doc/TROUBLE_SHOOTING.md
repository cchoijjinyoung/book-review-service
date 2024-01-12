# Trouble Shooting

프로젝트를 진행하면서 발생한 문제점들과 해결법 서술합니다.

## 스프링 시큐리티 + 컨트롤러 테스트
- Controller 테스트를 `@WebMvcTest` 어노테이션을 사용하여 작성하였다.
- `@AutoConfigureMockMvc(addFilter = true)`가 디폴트값이였고, 이 때 자동 등록되는 시큐리티 설정은 스프링 시큐리티에서 디폴트로 제공하는 설정이다. 그러다보니, csrf는 비활성화되있지 않았고, "/" 요청을 제외한 모든 요청은 인증이 필요했다. 그래서 필터를 다 제거해보려고 `@AutoConfigureMockMvc(addFilter = false)`로 설정하였더니, `(@AuthenticationPrincipal UserDetails)`에 대해 `@WithMockUser`와 같은 어노테이션 으로 테스트 할 수가 없었다.
- 그래서 자문을 구한 뒤, 인증이 필요한 앤드포인트를 테스트하는 클래스와, 인증이 필요하지 않은 앤드포인트를 담은 클래스로 나눠서 테스트하는 것으로 구현하였다.
- 인증이 필요한 앤드포인트를 포함하는 클래스에서는 `@WebMvcTest` 어노테이션만 추가하였다. 다만, 아직 csrf 보호가 활성화 되어있다는 점이 문제였다.

```java
// csrf 보호가 활성화 되어있음.
@WebMvcTest(ReviewController.class)
class ReviewControllerAuthenticatedTest {
    // 생략 ...//

    @Test
    void test() {
        mockMvc.perform(
                patch("/reviews/" + reviewId)
                    .with(csrf()) // 이 코드를 매번 추가시켜줘야함.
    }
```

그래서 아래와 같이 수정하였더니,
```java
@SpringBootTest
class ReviewControllerAuthenticatedTest {
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }
```

`with(csrf())`의 중복은 제거할 수 있었다.
다만 이번에는 `@SpringBootTest` 를 쓰게되니 테스트 속도가 많이 느려진다는 점이 단점이었다.

- 인증이 필요하지 않은 앤드포인트를 포함한 클래스는 아래와 같이 구현하였다.
```java
// 방법 1
@WebMvcTest(
    controllers = ReviewController.class,
    excludeAutoConfiguration = {
        UserDetailsServiceAutoConfiguration.class,
        SecurityAutoConfiguration.class
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                SecurityConfig.class, AuthenticationFilter.class
            })
    }
)
class ReviewControllerPermitRequestTest {

// 방법 2
@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerPermitRequestTest {
```

- 아래와 같은 방법으로는 필터를 아예 제거해버리기때문에 작은 프로젝트에서는 간단하게 사용하지만, 프로젝트가 커질수록 위험할 수 있다. 라는 생각을 들었다. 결국 서비스가 실제로 동작할 때와 똑같은 상황을 테스트 해야된다고 생각하는데, `@SpringBootTest`를 사용하자니 너무 느린 것 같다는 생각이 든다.
- - -
## 리뷰 좋아요 시, 리뷰작성자에게 실시간 알림 기능
- 처음 사용해보는 기술이어서 구현하는데 애를 먹었다. 전체적인 플로우와 작은 문제들을 작성해보았다.
- SSE(Server-Sent Events) + Redis pub/sub 사용하였다.
- 스프링에서 제공하는 SseEmitter(SSE 전송 방식 구현체)를 사용.
  - 서버에서 클라이언트로 데이터를 전송할 때 사용하는 객체.
  - 이를 통해 실시간으로 데이터를 전송할 수 있다.
### 플로우
- 클라이언트에서 서버로 SSE 연결 요청을 보낸다.
  - 로그인 한 회원에 한정하여 요청한다.
  - 새로고침을 하거나 새 브라우저 창에서는 SSE 요청이 끊기기 때문에, 웹페이지가 새로 로드되면 요청을 보내게끔한다.
- 서버는 SseEmitter객체를 생성 및 저장하고 클라이언트에게 응답해준다.
  - 로그인 한 회원의 정보를 가져오기 위해, `@AuthenticationPrincipal` 어노테이션을 사용하였다. 이 때 `userId`를 사용할 것이기 때문에, `CustomUserDetails`에 id 필드를 추가하였다.
  - SseEmitter의 타임아웃은 Access-Token의 타임아웃과 동일하게 설정해주었다.(3시간)
  - SseEmitter 저장소를 생성하고, 키(key)로 `userId`를, 값(value)로는 생성한 SseEmitter 객체를 저장한다.
  - 로직이 무사히 수행되면 "Connect 성공"이라는 name으로 SseEmitter 더미 데이터를 응답해줌으로써 연결을 확인 및 서버 오류를 방지한다.
- 'A 회원'이 'B 회원'의 리뷰에 좋아요를 누른다.
  - 유효성 검사 후 likeCount를 증가시키고 증가된 likeCount를 응답한다.
  - `ApplicationEventPublisher` '실시간 알림 이벤트'를 발생시킨다.(알림생성: "A님이 회원님의 리뷰를 좋아합니다.")
  - 알림의 `receiverId` 를 추출하여 SseEmitter를 찾는다.(SseEmitter에 연결된 회원이 '좋아요 알림'을 받아야하므로)
  - 찾은 SseEmitter 객체에 알림데이터를 담아서 `sseEmitter.send()`한다.
- 다만, 위의 경우 다중서버에서는 작동하지 않는다.
  - SseEmitter는 HTTP 요청을 처리하는 동안 생성되고, 이벤트가 전송되는 동안 유지되며, 타임아웃이되면 소멸하는 객체이기 때문에 인메모리 저장소와 비슷한 방식으로 동작하기 때문이다.
  - 즉, SseEmitter는 WAS 인스턴스 메모리내에만 존재한다.
  - 회원 A의 SseEmitter가 WAS-1에만 존재하면, 리뷰 좋아요 요청을 받은 WAS-2에서는 해당 알림을 받아야할 회원의 SseEmitter를 찾을 수 없게된다.
- 그래서 Redis pub/sub을 같이 사용한다.
  - B유저(id:1)가 로그인에 성공하면 Redis에 채널이름을 `notification:{id}` => "notification:1" 과 같이 생성하고, WAS는 생성된 Redis 채널을 구독한다.
  - 리뷰 좋아요 발생 시, 작성자의 id값을 통해 채널에 알림을 보낸다(publish).
  - 해당 채널을 구독한 WAS는 publish된 알림데이터를 받을 수 있다. 그 후에는 알림데이터에서 `receiverId`를 추출하여 SseEmitter를 찾고 데이터를 담아서 `send()`한다.

### 이슈
- 내 프로젝트내에서는 Redis pub/sub의 채널을 구독하는 과정을 로그인 성공 시 동작하도록 하였다.
  - 또한, 로그아웃 시에는 구독 취소가 동작한다.
  - 그러다보니 로그인시와 로그아웃시에 채널이름을 생성하기 위해 현재 로그인한 `userId`가 필요했다.
  - 내 프로젝트의 인증 과정은 `JwtAuthenticationFilter`에서 액세스/리프래시 토큰을 검증하고, 인증 객체를 생성하고, `SecurityContextHolder`에 저장함으로서 이루어진다.
  - `LoginHandler`의 경우, `JwtAuthenticationFilter`보다 뒤에서 동작하기 때문에, context에 저장된 `Authentication` 객체를 사용하여 `userId`를 가져올 수 있었다.
  - 다만, `LogoutHandler`는 `JwtAutheticationFilter`보다 앞에서 동작하고 있었기에, `userId`를 가져오지 못했다.
  - 아무리 구글링을 해봐도 `LogoutHandler`를 `JwtAutheticationFilter` 앞에 두는 글은 찾지 못했다.
  - 그래도 내 생각엔 앞에 두는 게 맞을 것 같아서 `LogoutHandler` 직전에 인증하도록 변경하였다.
- 내 프로젝트의 경우 Redis에 알림데이터를 publish할 때, `NotificationResponseDto` 객체를 발행한다.
  - 해당 객체의 필드에는 '읽음 유무'를 파악하기 위해 boolean타입의 `isRead`를 정의하였는데, 구독자가 데이터를 가져오면서 역직렬화할때, `isRead`가 아닌 `read`로 가져와졌다.
  - 대체로 직렬화하는 라이브러리들은 'getter 메서드' 이름을 기반으로 동작한다고 한다.
  - 근데 내가 만든 필드는 이미 `isRead`라서 'getter 메서드'가 `isIsRead`가 아닌, `isRead()`로 생성됐다.
  - Redis의 역직렬화 과정에서, `isRead()` 에서 is를 뺀 `read`로 변환시킨 것이다.
  - 필드명을 isRead -> readStatus 로 변경하였다.
- 또한 구독자(WAS)가 메세지를 받을 때, `Message` 객체로 받게되는데, 이 객체에서 채널명과, publish된 데이터를 꺼낼 수 있다.
```java
byte[] channel = message.getChannel();
byte[] data = message.getBody();
```
- 위 처럼 가져올 수 있는데, 다시 `NotificationResponseDto` 로 변환하기 위해 아래와 같이 `ObjectMapper`를 사용했다.
```java
String jsonData = new String(message.getBody(), StandardCharsets.UTF_8);
NotificationResponseDto data = objectMapper.readValue(jsonData, NotificationResponseDto.class);
```

- 이것 또한 되지않았다. 그래서 내가 정의한 RedisTemplate의 Serializer를 가져와서 사용했더니 잘 동작하였다. 코드는 다음과 같다.
```java
RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
NotificationResponseDto data = (NotificationResponseDto) serializer.deserialize(message.getBody());
```

- 아래는 Redis 클라이언트에서 notification:1 채널에 실시간으로 도착한 알림메세지이다.
```
> docker exec -it book-review-redis redis-cli

127.0.0.1:6379> SUBSCRIBE notification:1
1) "subscribe"
2) "notification:1"
3) (integer) 1
1) "message"
2) "notification:1"
3) "[\"com.topy.bookreview.api.dto.NotificationResponseDto\",
{\"id\":[\"java.lang.Long\",31],
\"content\":\"com.topy.bookreview.api.domain.entity.Member@4149d1d4\xeb\x8b\x98\xec\x9d\xb4 
\xed\x9a\x8c\xec\x9b\x90\xeb\x8b\x98\xec\x9d\x98 \xeb\xa6\xac\xeb\xb7\xb0\xeb\xa5\xbc 
\xec\xa2\x8b\xec\x95\x84\xed\x95\xa9\xeb\x8b\x88\xeb\x8b\xa4\",
\"readStatus\":false,\"redirectUrl\":\"/reviews/1\",
\"createdAt\":[\"java.time.LocalDateTime\",[2024,1,13,5,29,14,164174000]]}]"
```

- 아래는 redis-cli에서 publish 하는 방법 및 현재 존재하는 채널을 확인하는 방법이다.
```
// publish
PUBLISH <채널명> <메세지>

// 채널 조회
PUBLISH CHANNELS or PUBLISH CHANNELS "noti*" 

// 채널이 존재하면:
1) "notification:1"
2) "notification:2"

// 채널이 존재하지 않으면: 
(empty array)
```