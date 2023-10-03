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

### Content Negotiation
- 동일한 resource여도 사용자는 다른 형태의 결과값을 원할 수도 있다. 예를 들어, json이 아닌 XML형태!
- springboot에서는 아래 라이브러리는 추가해서 XML응답을 할 수 있다.
```java
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

### internationalization - i18n
- rest api 국제화
- 주로 HTTP Request Header로 'Accept-Language'을 사용
    - Accept-Language에서 언어 선택 가능
    - 요청 받은 언어를 바탕으로 응답
    - 예) en(English)이면 Good Morning으로 응답
    - `HelloWorldController`에 예시 구현

### Versioning REST API
- rest api에서 기능이 크게 바뀌는 경우 사용자들을 위해 함부로 바꿀 수 없다.
- 그래서 아래의 방법들로 versioning을 하는 것이다.
  - URL기반으로 versioning
  - Request parameter
  - Header
  - Media type
- URL기반 예시
```java
@RestController
public class VersioningPersonController {
    
  @GetMapping("/v1/person")
  public PersonV1 getFirstVersionOfPerson () {
    return new PersonV1("Kim MinJae");
  }

  @GetMapping("/v2/person")
  public PersonV2 getSecondVersionOfPerson () {
    return new PersonV2(new Name("Kim", "MinJae"));
  }
}
```
- Request parameter 예시
```java
@RestController
public class VersioningPersonController {
    
    @GetMapping(path = "/person", params = "version=1")
    public PersonV1 getFirstVersionOfPersonRequestParameter () {
        return new PersonV1("Kim MinJae");
    }
}
```
- Accept-Header 예시
```java
@RestController
public class VersioningPersonController {

    @GetMapping(path = "/person/header", produces = "application/vnd.company.app-v1+json")
    public PersonV1 getFirstVersionOfPersonAcceptHeader () {
        return new PersonV1("Kim MinJae");
    }
}
```

### HATEOAS
- hypermedia as the engine of application state
- rest api 사용자들에게 action을 알려주는 것
- 이번에는 HAL이라는 api 리소스간의 hyperlink를 전달하는 포맷을 이용한다.
  - `_links:{}`
- 이번 commit 코드를 통해서 아래처럼 모든 유저를 보고 싶은 경우, 아래처럼 정보를 전달 할 수 있다.
```json
{
  "id": 1,
  "name": "A",
  "birthdate": "1993-09-21",
  "_links": {
    "all-users": {
      "href": "http://localhost:8080/users"
    }
  }
}
```
- 코드
```java
@GetMapping("/users/{id}")
public EntityModel<User> retrieveUser(@PathVariable int id) {
    User user = service.findOne(id);

    if (user == null)
        throw new UserNotFoundException("id:" + id);

    EntityModel<User> entityModel = EntityModel.of(user);
    WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
    entityModel.add(link.withRel("all-users"));

    return entityModel;
}
```

### REST API 커스텀 - 정적 필터링
- serialization: object를 json, xml같은 것으로 바꾸는 것
  - 예를 들어, spring bean을 return 하여 그들의 property들이 json으로 전달
- java에서 관련 라이브러리는 Jackson이고 이를 이용하여 REST API의 reponse를 커스텀할 수 있다.
  - `@JSONProperty`로 field name 바꾸기
  - 원하는 field만 보내기 (filtering)
    - static: 모든 rest api에 적용 (`@JsonIgnore`, `@JsonIgnoreProperties`)
    - dynamic: 특정 rest api에 적용

### REST API 커스텀 - 동적 필터링
- 먼저, Bean 클래스를 `@JsonFilter("DynamicFilter")`로 감싼다.
- 그리고 아래와 같이 코드를 이용한다.
```java
@GetMapping("/filtering-dynamic")
public MappingJacksonValue filteringDynamic() {
    SomeBean someBean = new SomeBean("val1", "val2", "val3");
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(someBean);
    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1"); // field1만 남음
    FilterProvider filters = new SimpleFilterProvider().addFilter("DynamicFilter", filter);
    mappingJacksonValue.setFilters(filters);
    return mappingJacksonValue;
}
```

### Spring Boot Actuator로 API 모니터링
- 개발한 app을 monitor하고 manage하는 역할
- 제공하는 endpoints
  - beans, health, metrics, mappings 등
- app을 실행하고 위에 해당하는 url에 들어가면 해당하는 정보들을 보여준다.
- 더 많은 url은 `application.properties`에 `management.endpoints.web.exposure.include=*`를 추가한다.