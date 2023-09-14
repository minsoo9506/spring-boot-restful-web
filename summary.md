### hello world 예시
- `@RestController`로 통해서 rest 컨트롤러를 만든다.
- `@RequestMapping`으로 해당 path로 get method가 오면 해당 함수를 실행한다. 

```java
@RestController
public class HelloWorldController {
    @RequestMapping(method = RequestMethod.GET, path = "/hello-world")
    public String helloWorld() {
        return "Hello World";
    }
}

# GetMapping을 쓰는걸 더 추천
@RestController
public class HelloWorldController {
    @GetMapping(path = "/hello-world")
    public String helloWorld() {
        return "Hello World";
    }
}
```

### hello world 예시 - json을 받을 수 있게

- `HelloWorldBean` 객체를 return 했는데 json 형식의 `{'message':'Hello World'}`를 response로 준다 어떻게?
  - 결론적으로는 sprint boot가 알아서 자동으로 해주고 아래의 과정대로 진행된다.
  - 이와 관련한 것들은 sprint boot starter web을 우리가 사용해서 가능한 것이다.

1. how request handled?
   - sprint MVC 패턴에서 모든 요청은 DispatcherServlet으로 간다.
   - 그럼 url을 확인하고 알맞은 controller의 메서드로 처리한다.
2. 그럼 어떻게 Java bean을 json으로 변환?
   - `@RestController`에 들어가면 `@ResponseBody`가 있다.
   - 이는 Java bean을 그대로 보내게 하고 이를 `JacksonHttpMessageConverters`가 자동으로 바꿔준다.

### pathvariable
- url에서 `{}`로 감싼 변수가 pathvariable이고 이를 `@PathVariable`으로 함수에서 사용
```java
@RestController
public class HelloWorldController {

    @GetMapping(path = "/hello-world/{name}")
    public HelloWorldBean helloWorldPathVariable(@PathVariable String name) {
        return new HelloWorldBean(
                String.format("Hello World, $s", name)
        );
    }
}

```

# sns 만들기
- user, user's post에 대해 REST API를 만든다.

### REST API
- GET: retrieve details of a resource
- POST: create a new resource
- PUT: update an exising resource
- PATCH: update part of a resource
- DELETE: delete a resource

### POST에서 HTTP상태와 location
- post로 user를 생성하면 response로 201과 location에 user정보 전달
```java
@PostMapping("/users")
public ResponseEntity<Object> createUser(@RequestBody User user) {
    User savedUser = service.save(user);
    
    // post하고 response location부분에 저장한 user 보여주기
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedUser.getId())
            .toUri();

    return ResponseEntity.created(location).build();
}
```

- REST API에서 유효성 검증
  - 함수 인자 앞에 `@Valid`를 추가하고
  - 아래와 같이 유효성 조건을 추가할 수 있다.
```java
@PostMapping("/users")
public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
    User savedUser = service.save(user);

    // post하고 response location부분에 저장한 user 보여주기
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedUser.getId())
            .toUri();

    return ResponseEntity.created(location).build();
}
```
```java
public class User {
    private Integer id;

    @Size(min = 2, message = "Name should have at least 2 characters.")
    private String name;
    ...
```

### REST API documentation
- 사용자들이 잘 이해할 수 있는 doc이 필요하다.
- 어려운 점
  - code와의 싱크
  - 다양한 rest api 문서간의 일관성
- 아래 라이브러리를 통해서 swagger ui를 제공받을 수 있다.
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.0</version>
</dependency>
```
- application을 실행하면
  - http://localhost:8080/swagger-ui/index.html 에서 swagger ui 제공된다.
  - http://localhost:8080/v3/api-docs 에서는 openapi specification을 제공한다.
    - REST API 사양 설명
