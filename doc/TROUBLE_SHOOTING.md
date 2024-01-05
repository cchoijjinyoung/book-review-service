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