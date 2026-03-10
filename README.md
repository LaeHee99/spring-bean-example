# Spring Bean Practice (example01 ~ example19)

이 프로젝트는 **Spring Bean / DI / 설정 / 스코프 / 생명주기 / 조건부 Bean / 프로파일 / 설정 바인딩 / 검증**을
**직접 실습하며 학습**하기 위한 예제 모음입니다.

---

## 학습 방법

### 1단계: 읽기
각 예제의 코드를 읽고 주석을 통해 개념 이해

### 2단계: 실행
IDE에서 각 예제의 Main 클래스를 직접 실행하여 콘솔 출력과 동작 확인

### 3단계: 변경 (가장 중요)
"직접 해보기" 섹션의 과제를 수행하고, 코드를 수정하여 재실행하여 차이점 확인

### 4단계: 디버깅
"확인 포인트" 섹션의 내용을 디버거로 확인하고, 브레이크포인트를 걸고 Bean의 상태 관찰

---

# 예제별 상세 가이드

## example01 — @Configuration + @Bean 기본 등록

### 학습 목표
- Spring Boot 없이 순수 Spring 컨테이너를 직접 생성하는 방법
- @Configuration과 @Bean으로 수동 Bean 등록하는 방법
- Bean을 이름으로 조회하는 방법

### 실행 방법
```
IDE에서 실행:
src/main/java/com/codeit/springbeanpractice/example01/SpringBeanPracticeApplication.java
→ 우클릭 → Run
```

### 핵심 개념
- `@Configuration`: 스프링 설정 클래스임을 표시
- `@Bean`: 메서드가 반환하는 객체를 스프링 Bean으로 등록
- `AnnotationConfigApplicationContext`: Java 설정 기반 컨테이너 생성
- Bean 이름은 **메서드 이름**이 기본값

### 직접 해보기

#### 과제 1: 새로운 Bean 추가하기
**목표**: Account 타입 Bean을 추가로 등록해보세요

1. `ContextConfiguration.java` 파일 열기

2. 다음 import 문 추가:
```java
import com.codeit.springbeanpractice.example01.entity.Account;
import com.codeit.springbeanpractice.example01.entity.PersonalAccount;
```

3. 다음 메서드 추가:
```java
@Bean
public Account createAccount() {
    return new PersonalAccount("999-888-777", 5000000, 101);
}
```

4. `SpringBeanPracticeApplication.java`에 import 추가:
```java
import com.codeit.springbeanpractice.example01.entity.Account;
```

5. `SpringBeanPracticeApplication.java`의 main 메서드 안에 조회·출력 코드 추가:
```java
Account account = context.getBean("createAccount", Account.class);
System.out.println("account = " + account);
```

6. 실행하여 결과 확인
   - **기대 출력**: `account = ...` 형태로 Account 객체 정보 출력 (PersonalAccount에 toString()이 없으면 클래스명@해시 형태로 나옴)
   - **확인 사항**: Bean 이름이 메서드 이름(`createAccount`)과 동일

#### 과제 2: Bean 이름 변경하기
**목표**: @Bean의 name 속성 이해

1. `ContextConfiguration.java`에서 `createAccount()` 메서드에 `@Bean(name = "mySpecialAccount")` 로 변경
2. `SpringBeanPracticeApplication.java`에서 조회 시 이름을 `"mySpecialAccount"`로 변경
3. 실행하여 정상 동작 확인
   - **기대 출력**: 동일하게 Account 객체 정보 출력
   - **학습 포인트**: Bean 이름이 메서드 이름이 아니라 name 속성 값이 됨

#### 과제 3: Bean을 못 찾는 오류 만들어보기
**목표**: NoSuchBeanDefinitionException 이해

1. `SpringBeanPracticeApplication.java`의 main 메서드 안에서 존재하지 않는 Bean 조회:
```java
context.getBean("nonExistentBean");  // ← 오류 발생!
```

2. 어떤 예외가 발생하는지 확인
   - **기대 출력**: `NoSuchBeanDefinitionException` 발생
   - **예외 메시지**: "No bean named 'nonExistentBean' available" 형태

3. 예외 메시지를 읽고 무엇이 문제인지 파악

### 확인 포인트

**디버거 사용법**:
1. `context.getBean()` 줄에 브레이크포인트 설정 (줄 번호 클릭)
2. Debug 모드로 실행 (벌레 아이콘 또는 Shift+F9)
3. Variables 창에서 `context` 객체 펼쳐보기
4. `beanFactory` → `beanDefinitionMap` 확인
   - 등록된 모든 Bean의 이름과 정의를 볼 수 있음

**확인할 것**:
- Bean 이름이 메서드 이름과 일치하는가?
- `configurationSection02` Bean도 등록되어 있는가? (@Configuration 클래스 자체)

### 추가 도전

**여러 개의 Configuration 클래스**:
- 새로운 @Configuration 클래스 생성 (예: `SecondConfig`)
- `AnnotationConfigApplicationContext(ContextConfiguration.class, SecondConfig.class)`
- 여러 설정 클래스를 동시에 로드 가능

**같은 타입, 다른 이름**:
- MemberDTO를 반환하는 @Bean 메서드 2개 만들기
- 이름을 다르게 하여 구분
- 타입으로 조회하면 오류 발생하는지 확인

---

## example02 — @Configuration과 CGLIB 프록시

### 학습 목표
- @Configuration이 싱글톤을 보장하는 원리 이해
- CGLIB 프록시의 역할 이해
- @Bean 메서드 간 호출 시 싱글톤이 유지되는 이유 파악

### 실행 방법
```
example02/SpringBeanPracticeApplication.java 실행
```

### 핵심 개념
- `@Configuration` 클래스는 **CGLIB 프록시**로 감싸짐
- `@Bean` 메서드를 다른 `@Bean` 메서드에서 호출해도 **항상 같은 인스턴스 반환**
- 프록시가 메서드 호출을 가로채서 컨테이너에서 Bean을 조회

### 직접 해보기

#### 과제 1: 싱글톤 확인하기
**목표**: 같은 Bean을 여러 번 조회해도 같은 인스턴스인지 확인

1. `example02/SpringBeanPracticeApplication.java`의 main 메서드 안에 아래 코드 추가:
```java
MemberDTO member1 = context.getBean("getMember", MemberDTO.class);
MemberDTO member2 = context.getBean("getMember", MemberDTO.class);
System.out.println("같은 객체인가? " + (member1 == member2));
```

2. 실행하여 결과 확인
   - **기대 출력**: `같은 객체인가? true`
   - **학습 포인트**: 스프링은 기본적으로 싱글톤 패턴을 사용

#### 과제 2: Account Bean도 싱글톤인지 확인
**목표**: MemberDTO에 주입된 Account와 직접 조회한 Account가 같은 객체인지 확인

1. `SpringBeanPracticeApplication.java`의 main 메서드 안에 추가:
```java
MemberDTO member = context.getBean("getMember", MemberDTO.class);
Account account1 = member.getPersonalAccount();  // MemberDTO 안의 Account

Account account2 = context.getBean("accountGenerator", Account.class);  // 직접 조회

System.out.println("같은 Account 객체인가? " + (account1 == account2));
```

2. 실행하여 결과 확인
   - **기대 출력**: `같은 Account 객체인가? true`

**왜 true가 나올까?**
- `getMember()` 메서드 안에서 `accountGenerator()`를 호출
- 프록시가 이를 가로채서 컨테이너에서 Bean 반환
- 따라서 항상 같은 싱글톤 인스턴스

#### 과제 3: @Component로 바꾸면? (의도적 실험)
**목표**: @Configuration의 중요성 이해

1. `ContextConfiguration.java`에서 `@Configuration`을 주석 처리
2. `@Component`로 변경:
```java
//@Configuration
@Component
public class ContextConfiguration {
```

3. 과제 2를 다시 실행
4. 결과 확인
   - **기대 출력**: `같은 Account 객체인가? false`

**왜 false가 나올까?**
- @Component는 프록시를 만들지 않음
- `accountGenerator()` 호출이 일반 메서드 호출이 됨
- 매번 새로운 객체 생성

5. 실험 후 다시 @Configuration으로 복구

### 확인 포인트

**디버거 확인**:
1. `context.getBean()` 줄에 브레이크포인트
2. Configuration 클래스의 실제 타입 확인:
```java
Object config = context.getBean("contextConfiguration");
System.out.println(config.getClass().getName());
```
   - **기대 출력**: `ContextConfiguration$$EnhancerBySpringCGLIB$$...` 형태 (프록시)

### 추가 도전

**모든 Bean 출력하기**:
```java
Arrays.stream(context.getBeanDefinitionNames())
      .forEach(System.out::println);
```
어떤 Bean들이 등록되어 있는지 확인

---

## example03 — @ComponentScan 스캔 범위 지정

### 학습 목표
- @ComponentScan으로 자동 스캔 범위를 지정하는 방법
- basePackages vs basePackageClasses 차이점
- import와 스캔의 관계 이해

### 실행 방법
```
example03/SpringBeanPracticeApplication.java 실행
```

### 핵심 개념
- `@ComponentScan(basePackages = "패키지명")`: 해당 패키지와 하위 패키지 스캔
- import는 컴파일 시점의 참조일 뿐, 스캔과 무관
- 스캔은 **클래스패스**와 **애노테이션** 기준

### 직접 해보기

#### 과제 1: 스캔 범위 축소
**목표**: service 패키지만 스캔하면 repository는 Bean이 안 되는지 확인

1. `SpringBeanPracticeApplication.java` 수정:
```java
@ComponentScan(basePackages = "com.codeit.springbeanpractice.example03.service")
```

2. 실행
   - **기대 결과**: 오류 발생
   - **오류 메시지**: repository Bean을 찾을 수 없다는 메시지

3. 오류 메시지에서 어떤 Bean을 못 찾는지 확인

#### 과제 2: 새로운 Controller 추가
**목표**: 스캔 범위에 포함되는 Bean 추가

1. example03 패키지에 `MemberController.java` 생성:
```java
package com.codeit.springbeanpractice.example03;

import org.springframework.stereotype.Controller;

@Controller
public class MemberController {
    public MemberController() {
        System.out.println("MemberController 생성됨!");
    }
}
```

2. @ComponentScan의 basePackages에 **controller가 있는 패키지**를 포함 (MemberController가 example03 패키지 루트에 있으면 해당 패키지 추가):
```java
@ComponentScan(basePackages = {
    "com.codeit.springbeanpractice.example03.service",
    "com.codeit.springbeanpractice.example03.repository",
    "com.codeit.springbeanpractice.example03"  // controller 스캔
})
```

3. 실행하여 "MemberController 생성됨!" 메시지 확인

### 추가 도전

**여러 패키지 동시 스캔**:
```java
@ComponentScan(basePackages = {
    "com.codeit.springbeanpractice.example03.service",
    "com.codeit.springbeanpractice.example03.repository",
    "com.codeit.springbeanpractice.example03.controller"
})
```

---

## example04 — ComponentScan excludeFilters

### 학습 목표
- 특정 Bean을 스캔에서 제외하는 방법
- FilterType.REGEX와 ASSIGNABLE_TYPE 차이점
- 스캔 제외 패턴 작성법

### 실행 방법
```
example04/SpringBeanPracticeApplication.java 실행
```

### 핵심 개념
- `excludeFilters`: 스캔 대상에서 특정 클래스 제외
- `FilterType.REGEX`: 정규식으로 FQCN(Full Qualified Class Name) 매칭
- `FilterType.ASSIGNABLE_TYPE`: 특정 클래스 타입 지정

### 직접 해보기

#### 과제 1: ChannelRepository도 제외해보기
**목표**: excludeFilters에 여러 패턴 추가

1. `SpringBeanPracticeApplication.java` 수정:
```java
@ComponentScan(
    basePackages = "com.codeit.springbeanpractice.example04",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*Member.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ".*Channel.*"  // ← 추가
        )
    }
)
```

2. 실행
   - **기대 결과**: 모든 Bean이 제외되어 오류 발생

#### 과제 2: ASSIGNABLE_TYPE으로 변경
**목표**: 타입 기반 제외 방법 학습

1. `SpringBeanPracticeApplication.java`에 import 추가:
```java
import com.codeit.springbeanpractice.example04.repository.MemberRepository;
```

2. excludeFilters 안의 **.*Member.*** REGEX 필터 하나를 아래 ASSIGNABLE_TYPE으로 교체:
```java
@ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = MemberRepository.class
)
```
   - **참고**: `.*Channel.*` 필터를 그대로 두면 ChannelService·ChannelRepository도 제외되므로, "channelService Bean 조회 성공"을 보려면 `.*Channel.*` 필터를 제거한 상태에서 실행하세요.

3. 실행하여 MemberRepository만 제외되고 나머지는 조회되는지 확인
   - **기대 출력**: "channelService Bean 조회 성공" 메시지

#### 과제 3: includeFilters 사용해보기
**목표**: 화이트리스트 방식 스캔

1. import 추가:
```java
import com.codeit.springbeanpractice.example04.service.ChannelService;
import com.codeit.springbeanpractice.example04.repository.ChannelRepository;
```

2. 기본 스캔을 끄고 필요한 것만 포함:
```java
@ComponentScan(
    basePackages = "com.codeit.springbeanpractice.example04",
    useDefaultFilters = false,  // ← 기본 스캔 끄기
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {ChannelService.class, ChannelRepository.class}
        )
    }
)
```

### 확인 포인트

**실행 결과 분석**:
- "channelService Bean 조회 성공" 메시지 확인
- "예상대로 memberRepository Bean은 스캔에서 제외되어 조회 실패" 메시지 확인
- 제외된 Bean을 조회하려고 하면 `NoSuchBeanDefinitionException` 발생

---

## example05 — @SpringBootApplication + 모든 Bean 출력 + ObjectMapper 커스터마이징

### 학습 목표
- @SpringBootApplication이 어떤 Bean들을 자동으로 등록하는지 확인
- 스프링 부트의 자동 설정(Auto Configuration) 이해
- @Configuration으로 기본 Bean을 커스터마이징하는 방법

### 실행 방법
```
example05/SpringBeanPracticeApplication.java 실행
```

### 핵심 개념
- `@SpringBootApplication` = `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`
- Auto Configuration: 스프링 부트가 자동으로 등록하는 수십~수백 개의 Bean
- 사용자 정의 @Bean으로 자동 설정 Bean을 오버라이드 가능

### 직접 해보기

#### 과제 1: 모든 Bean 이름 확인
**목표**: 스프링 부트가 자동으로 등록하는 Bean 목록 파악

1. 실행하여 콘솔 출력 확인
   - **기대 출력**: 수십 개의 Bean 이름들이 출력됨
   - **주요 Bean들**:
     - `springBeanPracticeApplication` (main 클래스)
     - `customWebConfig` (직접 만든 @Configuration)
     - `objectMapper` (직접 만든 @Bean)
     - `tomcatServletWebServerFactory` (내장 톰캣)
     - 기타 많은 자동 설정 Bean들

2. `SpringBeanPracticeApplication.java` 확인:
```java
Arrays.stream(context.getBeanDefinitionNames())
    .forEach(System.out::println);
```

3. 출력된 Bean 이름들 중 패턴 찾기:
   - `...AutoConfiguration`: 자동 설정 클래스들
   - `...Properties`: 설정 바인딩 클래스들
   - 사용자가 만든 Bean들 (objectMapper, customWebConfig 등)

#### 과제 2: 특정 Bean만 필터링하여 출력
**목표**: 원하는 Bean만 찾아보기

1. `SpringBeanPracticeApplication.java` 수정:
```java
System.out.println("=== 사용자 정의 Bean 목록 ===");
Arrays.stream(context.getBeanDefinitionNames())
    .filter(name -> name.contains("custom") || name.contains("object"))
    .forEach(System.out::println);
```

2. 실행
   - **기대 출력**:
     ```
     customWebConfig
     objectMapper
     ```

3. 다른 패턴도 시도:
```java
// AutoConfiguration Bean만 보기
.filter(name -> name.contains("AutoConfiguration"))

// Properties Bean만 보기
.filter(name -> name.contains("Properties"))
```

#### 과제 3: ObjectMapper Bean 커스터마이징 확인
**목표**: 직접 등록한 Bean이 스프링 부트 기본 Bean을 대체하는지 확인

1. `CustomWebConfig.java` 확인:
```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);  // 예쁘게 출력
    return mapper;
}
```

2. `SpringBeanPracticeApplication.java`의 main 메서드에 ObjectMapper 테스트 코드 추가:
```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

// 기존 코드 아래에 추가
ObjectMapper mapper = context.getBean(ObjectMapper.class);
try {
    String json = mapper.writeValueAsString(Map.of("name", "홍길동", "age", 30));
    System.out.println("JSON 출력:\n" + json);
} catch (JsonProcessingException e) {
    e.printStackTrace();
}
```

3. 실행
   - **기대 출력**:
     ```json
     JSON 출력:
     {
       "name" : "홍길동",
       "age" : 30
     }
     ```
   - INDENT_OUTPUT이 적용되어 보기 좋게 출력됨

4. CustomWebConfig의 @Bean 주석 처리 후 재실행:
```java
// @Bean
// public ObjectMapper objectMapper() {
```

5. 실행
   - **기대 출력**: `{"name":"홍길동","age":30}` (한 줄로 출력)
   - 기본 ObjectMapper가 사용됨

6. @Bean 주석 해제하여 복구

#### 과제 4: @SpringBootApplication에서 자동 설정 제외
**목표**: 특정 자동 설정을 끄는 방법 이해

1. `SpringBeanPracticeApplication.java`의 주석 확인:
```java
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
```

2. Spring Security 의존성이 있을 때 자동 설정을 제외하는 예시
   - 실제로는 build.gradle에 security 의존성이 없으므로 제외할 필요 없음

3. 다른 자동 설정 제외 예시 (import 필요):
```java
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,  // 데이터베이스 자동 설정 제외
    JacksonAutoConfiguration.class      // Jackson 자동 설정 제외
})
```

### 확인 포인트

**@SpringBootApplication의 구성 요소**:
```java
@SpringBootApplication
= @Configuration          // Bean 정의 가능
+ @ComponentScan          // 패키지 스캔
+ @EnableAutoConfiguration  // 자동 설정 활성화
```

**자동 설정 vs 수동 Bean**:
- 자동 설정: 스프링 부트가 조건부로 등록 (사용자 Bean이 없을 때만)
- 수동 Bean (@Bean): 자동 설정보다 우선순위가 높음

**디버거로 확인**:
1. context.getBean(ObjectMapper.class) 줄에 브레이크포인트
2. Variables 창에서 objectMapper 객체 확인
3. 클래스 이름이 `com.fasterxml.jackson.databind.ObjectMapper`인지 확인

### 추가 도전

**Bean 개수 세기**:
```java
long count = Arrays.stream(context.getBeanDefinitionNames()).count();
System.out.println("전체 Bean 개수: " + count);
```

**Bean 타입별 출력**:
```java
Arrays.stream(context.getBeanDefinitionNames())
    .forEach(name -> {
        Object bean = context.getBean(name);
        System.out.println(name + " : " + bean.getClass().getSimpleName());
    });
```

**JSON 직렬화 옵션 추가**:
```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 날짜를 ISO-8601 형식으로
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // null 값 제외
    return mapper;
}
```

---

## example06 — Controller → Service 생성자 주입

### 학습 목표
- 계층 구조에서 의존성 주입하는 방법
- 생성자 주입의 장점 이해
- @Autowired를 생략할 수 있는 경우

### 실행 방법
```
IDE에서 실행:
src/main/java/com/codeit/springbeanpractice/example06/Main.java
→ 우클릭 → Run
```

### 핵심 개념
- **생성자가 1개**면 `@Autowired` 생략 가능
- `final` 필드 사용으로 **불변성** 보장
- Controller → Service 의존 관계

### 직접 해보기

#### 과제 1: Repository 계층 추가하기
**목표**: 3계층 구조 완성 (Controller → Service → Repository)

1. example06 패키지에 `MemberRepository.java` 생성:
```java
package com.codeit.springbeanpractice.example06;

import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    public void save() {
        System.out.println("회원 저장됨!");
    }
}
```

2. `MemberService.java`에 Repository 주입:
```java
package com.codeit.springbeanpractice.example06;

import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void registerMember() {
        memberRepository.save();
    }
}
```

3. `Main.java`의 main 메서드 안에 아래 코드 추가 (기존 getBean 호출 아래 등):
```java
MemberService service = context.getBean(MemberService.class);
service.registerMember();
```

4. 실행하여 "회원 저장됨!" 메시지 확인
   - **기대 출력**: `회원 저장됨!`

#### 과제 2: 필드 주입으로 변경해보기 (비권장 방식 체험)
**목표**: 생성자 주입과 필드 주입 비교

1. `MemberController.java`를 필드 주입 방식으로 변경:
```java
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class MemberController {
    @Autowired  // ← 생략 불가
    private MemberService memberService;  // ← final 사용 불가
}
```

#### 과제 3: Setter 주입도 시도해보기
**목표**: Setter 주입 방식 체험

1. `MemberController.java`를 Setter 주입 방식으로 변경:
```java
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class MemberController {
    private MemberService memberService;

    @Autowired
    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }
}
```

### 추가 도전

**순환 참조 만들어보기** (안티 패턴):
1. MemberService에서 MemberController를 주입하려고 시도
2. 어떤 오류가 발생하는지 확인
   - **기대 결과**: `BeanCurrentlyInCreationException` 발생
3. 순환 참조는 설계 문제이므로 발생하지 않도록 주의

---

## example07 — Repository 인터페이스 + 구현체

### 학습 목표
- 인터페이스로 저장소를 추상화하는 방법
- Optional을 사용한 안전한 조회
- 구현체를 쉽게 교체할 수 있는 구조 이해

### 실행 방법
```
example07/Application.java 실행
```

### 핵심 개념
- **인터페이스로 추상화** → 구현체 교체 용이
- `Optional.orElseThrow()`: 값이 없으면 예외 발생
- Repository는 Service를 의존하면 안 됨 (계층 역전)

### 직접 해보기

#### 과제 1: MapBookRepository 활성화하기
**목표**: 인터페이스 구현체 교체 체험

1. `MapBookRepository.java`의 주석 전체 제거
2. `MemoryBookRepository.java`에 `@Primary` 추가:
```java
import org.springframework.context.annotation.Primary;

@Repository
@Primary  // ← 추가: 여러 구현체가 있을 때 우선 선택
public class MemoryBookRepository implements BookRepository {
```

3. 실행하여 어떤 Repository가 사용되는지 확인

#### 과제 2: 새로운 책 추가 기능 만들기
**목표**: CRUD 메서드 추가

1. BookRepository 인터페이스에 메서드 추가:
```java
void save(Book book);
```

2. MemoryBookRepository에 구현:
```java
@Override
public void save(Book book) {
    books.add(book);
    System.out.println("책 저장됨: " + book);
}
```

3. BookService에 메서드 추가:
```java
public void addBook(Book book) {
    bookRepository.save(book);
}
```

4. `Application.java`의 main 메서드 안에 테스트 코드 추가:
```java
BookService bookService = context.getBean(BookService.class);
bookService.addBook(new Book(4, "테스트 도서", "홍길동"));
```

5. 실행하여 결과 확인
   - **기대 출력**: `책 저장됨: Book{...}`

#### 과제 3: 책 삭제 기능도 추가
**목표**: 스스로 CRUD 메서드 완성

1. `BookRepository` 인터페이스에 `void deleteBySequence(int sequence)` 선언 후, 구현체·BookService·Application의 main 순서로 작성
2. 존재하지 않는 책을 삭제하려고 하면 예외 발생하도록 구현

### 확인 포인트

**디버거로 확인**:
1. `bookService.searchBookBySequence(1)` 줄에 브레이크포인트
2. `bookRepository` 필드 확인 → MemoryBookRepository 타입인지 확인
3. `books` 리스트의 내용 확인

---

## example08 — 동일 타입 Bean 여러 개 주입

### 학습 목표
- 같은 인터페이스의 구현체가 여러 개일 때 처리 방법
- @Primary, @Qualifier, @Resource 차이점
- Map/Set으로 여러 Bean 동시 주입

### 실행 방법
```
example08/PokemonApplication.java 실행
```

### 핵심 개념
- **@Primary**: 여러 Bean 중 기본으로 선택될 Bean 지정
- **@Qualifier**: 특정 Bean을 이름으로 지정
- **@Resource**: JSR-250 표준, 이름 기준 주입
- **Map<String, T>**: key = Bean 이름, value = 구현체
- **Set<T>**: 모든 구현체를 Set으로 주입

### 직접 해보기

#### 과제 1: @Primary 추가해보기
**목표**: @Qualifier 없이도 주입되도록 만들기

1. `Pikachu.java`에 import 추가 후 `@Primary` 추가:
```java
import org.springframework.context.annotation.Primary;

@Component
@Primary  // ← 추가
public class Pikachu implements Pokemon {
```

2. `PokemonService.java`에서 `@Qualifier` 제거:
```java
public PokemonService(Pokemon pokemon) {  // @Qualifier 제거
    this.pokemon = pokemon;
}
```

3. 실행
   - **기대 출력**: "백만볼트!" (Pikachu가 자동 선택됨)

#### 과제 2: @Qualifier를 다른 Pokemon으로 변경
**목표**: 원하는 구현체 선택

1. `@Qualifier("squirtle")`로 변경
2. 실행
   - **기대 출력**: "꼬북이 물대포!"

3. `@Qualifier("charmander")`로 변경
4. 실행
   - **기대 출력**: "파이리 불꽃화염!"

#### 과제 3: 새로운 Pokemon 추가하기
**목표**: 직접 구현체 작성

1. example08 패키지에 `Bulbasaur.java` 생성:
```java
package com.codeit.springbeanpractice.example08;

import org.springframework.stereotype.Component;

@Component
public class Bulbasaur implements Pokemon {
    @Override
    public void attack() {
        System.out.println("이상해씨 덩굴채찍!");
    }

    @Override
    public String name() {
        return "bulbasaur";
    }
}
```

2. 실행 (PokemonApplication이 PokemonServiceV2도 사용)하여 Map에 4개 포켓몬이 모두 출력되는지 확인
   - **기대**: charmander, pikachu, squirtle, bulbasaur 모두 출력

#### 과제 4: Set으로 주입해보기
**목표**: Set<Pokemon> 주입 방식 학습

1. `PokemonServiceV2`에 필드와 생성자 추가:
```java
private final Set<Pokemon> pokemonSet;

public PokemonServiceV2(Set<Pokemon> pokemonSet) {
    this.pokemonSet = pokemonSet;
}

public void attackAll() {
    pokemonSet.forEach(Pokemon::attack);
}
```

2. `PokemonApplication.java`의 main에서 `serviceV2.attackAll()` 호출 추가 후 실행하여 모든 포켓몬 공격 확인

### 확인 포인트

**디버거 확인**:
1. PokemonServiceV2의 `pokemons` Map 확인
2. key 값들이 Bean 이름과 일치하는지 확인
3. value들이 실제 구현체 인스턴스인지 확인

**콘솔 출력 예시**:
```
Key: charmander Value: Charmander@1a2b3c4d
파이리 불꽃화염!
Key: pikachu Value: Pikachu@5e6f7g8h
백만볼트!
Key: squirtle Value: Squirtle@9i0j1k2l
꼬북이 물대포!
```

---

## example09 — Optional로 선택적 의존성 주입

### 학습 목표
- Bean이 없을 수도 있는 상황에서 안전하게 의존성 주입하는 방법
- Optional<T> 타입 주입의 장점
- @Autowired(required=false), @Nullable과의 비교

### 실행 방법
```
example09/Main.java 실행
```

### 핵심 개념
- `Optional<T>`: Bean이 있으면 Optional에 담김, 없으면 Optional.empty() 주입
- `@Autowired(required=false)`: Bean이 없으면 주입하지 않음 (null)
- `@Nullable`: Bean이 없어도 null 주입 허용

### 직접 해보기

#### 과제 1: NotificationService가 있을 때
**목표**: Optional에 Bean이 담겨 있는지 확인

1. 실행하여 출력 확인 (Main에서 `DependencyService.testDependency()`가 호출되므로 그 결과가 콘솔에 출력됨)
   - **기대 출력**: `알람을 전송합니다.`
   - NotificationService Bean이 있으므로 `Optional.isPresent()` = true

2. `DependencyService.java` 확인:
```java
@Autowired
private Optional<NotificationService> notificationService;

public void testDependency() {
    if (notificationService.isPresent()) {
        notificationService.get().sendNotification();
    } else {
        System.out.println("No notification service available");
    }
}
```

#### 과제 2: NotificationService 주석 처리
**목표**: Bean이 없을 때 Optional.empty() 주입 확인

1. `NotificationService.java`의 @Service 주석 처리:
```java
// @Service  ← 주석 처리 (Bean으로 등록되지 않음)
public class NotificationService {
```

2. 실행
   - **기대 출력**: `No notification service available`
   - Optional.empty()가 주입되어 isPresent()가 false

3. 주석 해제하여 복구

#### 과제 3: @Autowired(required=false)로 변경
**목표**: required=false 방식 이해

1. `DependencyService.java` 수정 (Optional 타입 제거):
```java
@Autowired(required = false)
private NotificationService notificationService;  // Optional<> 제거!

public void testDependency() {
    if (notificationService != null) {
        notificationService.sendNotification();
    } else {
        System.out.println("No notification service available");
    }
}
```

2. NotificationService를 주석 처리하고 실행
   - **기대 출력**: `No notification service available`
   - notificationService 필드가 null로 유지됨

3. NotificationService 주석 해제하고 필드를 다시 Optional<>로 변경:
```java
@Autowired
private Optional<NotificationService> notificationService;
```

#### 과제 4: @Nullable 사용
**목표**: @Nullable 방식 이해

1. `DependencyService.java` 수정 (Optional 타입 제거 및 @Nullable 추가):
```java
import org.springframework.lang.Nullable;

@Autowired
@Nullable
private NotificationService notificationService;  // Optional<> 제거, @Nullable 추가

public void testDependency() {
    if (notificationService != null) {
        notificationService.sendNotification();
    } else {
        System.out.println("No notification service available");
    }
}
```

2. NotificationService를 주석 처리하고 실행
   - **기대 출력**: `No notification service available`
   - @Nullable이 있으면 Bean이 없어도 주입 시도 (null 허용)

3. NotificationService 주석 해제하고 필드를 다시 Optional<>로 변경하여 원래 상태로 복구

#### 과제 5: 세 가지 방식 비교 정리
**목표**: 각 방식의 장단점 이해

| 방식 | Bean 없을 때 | 장점 | 단점 |
|------|-------------|------|------|
| `Optional<T>` | Optional.empty() | null 체크 불필요, 함수형 스타일 | 타입이 복잡해짐 |
| `@Autowired(required=false)` | null | 간단함 | null 체크 필요 |
| `@Nullable` | null | IDE 경고 활용 | null 체크 필요 |

**권장**: `Optional<T>` (null-safe, 명시적)

### 확인 포인트

**디버거로 확인**:
1. NotificationService Bean이 있을 때 notificationService 필드 확인
   - Optional<NotificationService> 타입
   - value에 실제 NotificationService 인스턴스 담김

2. NotificationService Bean이 없을 때
   - Optional.empty() 확인

**실제 활용 사례**:
```java
@Service
public class EmailService {
    @Autowired
    private Optional<SmtpConfig> smtpConfig;

    public void sendEmail() {
        smtpConfig.ifPresent(config -> {
            // SMTP 설정이 있을 때만 이메일 전송
        });
    }
}
```

### 추가 도전

**Optional의 함수형 메서드 활용**:
```java
public void testDependency() {
    notificationService
        .map(service -> {
            service.sendNotification();
            return true;
        })
        .orElseGet(() -> {
            System.out.println("No notification service");
            return false;
        });
}
```

**여러 개의 선택적 의존성**:
```java
@Autowired
private Optional<EmailService> emailService;

@Autowired
private Optional<SmsService> smsService;

public void notifyUser() {
    emailService.ifPresent(EmailService::send);
    smsService.ifPresent(SmsService::send);
}
```

---

## example10 — session scope + scoped proxy

### 학습 목표
- Bean 스코프의 종류와 차이점 이해
- session 스코프의 동작 원리
- Scoped Proxy가 필요한 이유

### 실행 방법
```
example10/Main.java 실행
```

### 핵심 개념
- **singleton** (기본값): 애플리케이션 전체에서 1개 인스턴스
- **prototype**: 요청할 때마다 새로운 인스턴스 생성
- **request**: HTTP 요청당 1개 인스턴스
- **session**: HTTP 세션당 1개 인스턴스
- **proxyMode**: 싱글톤 Bean에 session/request 스코프 Bean을 주입하기 위해 필요

### 직접 해보기

#### 과제 1: prototype 스코프로 변경
**목표**: 매번 새로운 인스턴스가 생성되는지 확인

1. `SessionScopeBean.java` 수정: `@Scope`를 아래처럼 바꿈 (proxyMode는 prototype에서는 생략 가능):
```java
@Component
@Scope(scopeName = "prototype")  // session → prototype
public class SessionScopeBean {
}
```

2. `Main.java`의 main 메서드 안에 다음 코드 추가 (OrderService 조회·hello() 호출 아래에):
```java
SessionScopeBean bean1 = context.getBean(SessionScopeBean.class);
SessionScopeBean bean2 = context.getBean(SessionScopeBean.class);
System.out.println("같은 객체? " + (bean1 == bean2));
```

3. 실행
   - **기대 출력**: `같은 객체? false` (매번 새 인스턴스 생성)

4. **다음 과제를 위해** SessionScopeBean을 다시 `@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)` 로 되돌리고, Main에 추가한 bean1/bean2 조회 코드는 주석 처리하거나 제거

#### 과제 2: proxyMode 제거하면?
**목표**: 프록시의 필요성 이해

1. `SessionScopeBean.java`에서 `proxyMode`만 제거 (scopeName은 "session" 유지):
```java
@Scope(scopeName = "session")  // proxyMode 제거
```

2. 실행
   - **기대 결과**: 애플리케이션 기동 시 오류 발생
   - **오류 메시지**: 싱글톤 Bean(OrderService)에 session 스코프 Bean을 직접 주입할 수 없다는 내용

**왜 오류가 발생할까?**
- OrderService는 싱글톤 (애플리케이션 시작 시 1번 생성)
- SessionScopeBean은 세션마다 다른 인스턴스
- 싱글톤에 세션 스코프 Bean을 직접 주입하면 세션이 바뀌어도 같은 Bean 사용
- **proxyMode**를 사용하면 프록시 객체를 주입하고, 실제 호출 시점에 세션에 맞는 Bean 연결

3. 다시 proxyMode 복구

#### 과제 3: request 스코프로 변경
**목표**: request 스코프 적용 방법 확인

1. `SessionScopeBean.java`에서 scopeName만 변경:
```java
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
```
   - HTTP 요청마다 새로운 인스턴스가 생성됨. main()만 실행하는 환경에서는 실제 인스턴스 생성은 웹 요청이 있을 때 이루어짐.

### 추가 도전

**프록시 클래스 확인하기**:
```java
SessionScopeBean bean = context.getBean(SessionScopeBean.class);
System.out.println(bean.getClass().getName());
```
- **기대 출력**: `SessionScopeBean$$EnhancerBySpringCGLIB$$...` 형태

---

## example11 — Bean 생명주기 콜백

### 학습 목표
- Bean 생성 후 초기화 메서드를 실행하는 방법
- Bean 소멸 전 정리 메서드를 실행하는 방법
- 3가지 방식(@Bean 속성, 인터페이스, 애노테이션)의 차이점 이해

### 실행 방법
```
example11/Main.java 실행
```

### 핵심 개념
- **초기화 콜백**: Bean 생성 + 의존성 주입 완료 후 자동 실행
- **소멸 콜백**: 스프링 컨테이너 종료 시 자동 실행

**3가지 방식**:
1. `@Bean(initMethod="메서드명", destroyMethod="메서드명")`
2. `InitializingBean`, `DisposableBean` 인터페이스 구현
3. `@PostConstruct`, `@PreDestroy` 애노테이션 (권장)

### 직접 해보기

#### 과제 1: @Bean의 initMethod/destroyMethod 확인
**목표**: 설정 클래스에서 생명주기 메서드 지정

1. 실행하여 출력 확인
   - **기대**: 초기화 시 `MyBean을 생성했습니다.`, `MyComponent 빈을 초기화했습니다.`가 나온 뒤, `Hello Spring Container World`가 출력됨.
   - **소멸 콜백**(`바이바이 MyComponent Bean...`, `MyBean을 정리합니다.`)은 **애플리케이션 종료 시** 실행됨. IDE에서 Run으로 실행하면 웹 서버가 떠 있어서 프로세스를 종료(Ctrl+C 또는 중지 버튼)할 때 콘솔에 출력됨.

2. `AppConfig.java` 확인:
```java
@Bean(initMethod = "init", destroyMethod = "cleanup")
public MyBean myBean() {
    return new MyBean();
}
```

3. `MyBean.java`에서 init()과 cleanup() 메서드가 일반 메서드로 작성되어 있음을 확인
   - **학습 포인트**: Spring이 알아서 호출하므로 애노테이션이나 인터페이스 불필요

#### 과제 2: @PostConstruct와 @PreDestroy 사용
**목표**: 표준 애노테이션 방식 (가장 권장)

1. `MyComponent.java`에서 **@PostConstruct** 초기화 메서드 주석 해제 (이미 있는 `init()` 메서드의 주석 제거)

2. **@PreDestroy** 적용: 기존 `destroy()` 메서드에 `@PreDestroy` 애노테이션만 추가. 예:
```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@PostConstruct
public void init() {
    System.out.println("MyComponent 빈을 초기화했습니다.");
}

@PreDestroy  // ← 추가
public void destroy() {
    System.out.println("바이바이 MyComponent Bean...");
}
```

3. **afterPropertiesSet()** 메서드 전체 주석 처리 (과제 3에서 순서 확인 시 다시 해제)

4. 실행하여 동일한 결과 확인
   - **학습 포인트**: @PostConstruct / @PreDestroy가 더 명시적이고 표준 방식

#### 과제 3: 인터페이스 방식 확인
**목표**: InitializingBean과 DisposableBean 이해

1. `MyComponent.java`에서 **afterPropertiesSet()** 주석을 해제 (과제 2에서 주석 처리했다면 다시 활성화)

2. 현재 상태 확인 - `InitializingBean`, `DisposableBean` 구현 + @PostConstruct 초기화 메서드가 함께 있음

3. 실행하여 순서 확인:
   - **초기화**: @PostConstruct → afterPropertiesSet() (둘 다 있으면 @PostConstruct가 먼저 실행됨)
   - **소멸**: @PreDestroy → DisposableBean.destroy()

3. 코드에서 확인:
```java
public class MyComponent implements InitializingBean, DisposableBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        // 초기화 로직
    }

    @Override
    public void destroy() {
        // 소멸 로직
    }
}
```

#### 과제 4: 3가지 방식 혼합 사용
**목표**: 여러 방식을 동시에 사용할 때 실행 순서 파악

1. `MyComponent.java`의 **메서드 이름과 출력 문구만** 아래처럼 바꿈 (`@Component`, `implements InitializingBean, DisposableBean` 는 그대로 둠):
```java
@PostConstruct
public void postConstruct() {
    System.out.println("1. @PostConstruct 호출");
}

@Override
public void afterPropertiesSet() throws Exception {
    System.out.println("2. InitializingBean.afterPropertiesSet() 호출");
}

@PreDestroy
public void preDestroy() {
    System.out.println("1. @PreDestroy 호출");
}

@Override
public void destroy() {
    System.out.println("2. DisposableBean.destroy() 호출");
}
```
   - 기존 `init()`, `sayHello()` 등은 위 예시에 맞게 제거하거나 이름을 바꾸면 됨

2. 실행
   - **기대 초기화 순서**: @PostConstruct → afterPropertiesSet()
   - **기대 소멸 순서**: @PreDestroy → destroy() (애플리케이션 종료 시)

### 확인 포인트

**생명주기 콜백 실제 활용 사례**:
- 초기화: 데이터베이스 연결 풀 생성, 캐시 로딩, 외부 API 연결
- 소멸: 연결 종료, 임시 파일 삭제, 리소스 해제

**권장 방식**:
- 가장 권장: `@PostConstruct`, `@PreDestroy` (표준 자바 애노테이션)
- 설정 클래스 방식: 외부 라이브러리 Bean에 사용 (소스 수정 불가능할 때)
- 인터페이스 방식: 스프링에 종속적이므로 비권장

### 추가 도전

**비동기 초기화**:
```java
@PostConstruct
public void init() {
    CompletableFuture.runAsync(() -> {
        // 시간이 오래 걸리는 초기화 작업
    });
}
```

---

## example12 — @Value로 다양한 설정 값 주입

### 학습 목표
- @Value로 application.yml의 값을 필드에 주입하는 방법
- SpEL(Spring Expression Language) 사용법
- 기본값 설정, 타입 변환, 리스트 처리

### 실행 방법
```
example12/Main.java 실행
```

### 핵심 개념
- `@Value("${property.key}")`: 설정 값 주입
- `@Value("${key:기본값}")`: 값이 없을 때 기본값 사용
- `@Value("#{표현식}")`: SpEL로 연산, 메서드 호출 가능
- `@Value("#{'${key}'.split(',')}")`: 문자열을 리스트로 변환

### 직접 해보기

#### 과제 1: 기본 @Value 사용
**목표**: application.yml의 값을 필드에 주입

1. 실행하여 출력 확인
   - **기대 출력**:
     ```
     Server is running on port: 8080
     (doubleTimeout, username, isValid, servers 값들도 출력됨)
     ```

2. `InfoPrinter.java` 확인:
```java
@Value("Server is running on port: #{${server.port}}")
private String message;
```

3. application.yml에서 server.port 값 변경:
```yaml
server:
  port: 9090
```

4. 재실행
   - **기대 출력**: `Server is running on port: 9090`

#### 과제 2: 기본값 설정
**목표**: 설정 파일에 값이 없을 때 기본값 사용

1. `InfoPrinter.java` 확인:
```java
@Value("${my.username:anonymous}")
private String username;
```

2. application.yml에서 my.username 주석 처리:
```yaml
my:
  # username: admin  ← 주석 처리
```

3. 실행
   - **기대 출력**: `anonymous` (기본값 사용됨)

4. 주석 해제하고 재실행
   - **기대 출력**: `admin` (설정 값 사용됨)

#### 과제 3: SpEL로 연산하기
**목표**: Spring Expression Language로 계산

1. `InfoPrinter.java` 확인:
```java
@Value("#{${config.timeout} * 2}")
private int doubleTimeout;
```

2. application.yml의 config.timeout 값 변경:
```yaml
config:
  timeout: 5000
```

3. 실행
   - **기대 출력**: `10000` (5000 * 2)

4. 더 복잡한 SpEL 추가:
```java
@Value("#{${config.timeout} > 3000 ? '높음' : '낮음'}")
private String timeoutLevel;

public void printTimeoutLevel() {
    System.out.println(timeoutLevel);
}
```

5. Main 클래스에서 호출:
```java
context.getBean(InfoPrinter.class).printTimeoutLevel();
```

6. 실행
   - **기대 출력**: `높음` (5000 > 3000이므로)

#### 과제 4: 문자열을 리스트로 변환
**목표**: 콤마로 구분된 문자열을 List로 자동 변환

1. `InfoPrinter.java` 확인:
```java
@Value("#{'${my.servers}'.split(',')}")
private List<String> servers;
```

2. application.yml 확인:
```yaml
my:
  servers: "localhost,dev-server,prod-server"
```

3. 실행
   - **기대 출력**: `[localhost, dev-server, prod-server]`

4. servers 값 변경:
```yaml
my:
  servers: "server1,server2,server3,server4"
```

5. 재실행
   - **기대 출력**: `[server1, server2, server3, server4]`

#### 과제 5: boolean 타입 처리
**목표**: 문자열을 boolean으로 자동 변환

1. `InfoPrinter.java` 확인:
```java
@Value("${my.isValid:0}")
private boolean isValid;
```

2. application.yml의 my.isValid 값 변경:
```yaml
my:
  isValid: true
```

3. 실행
   - **기대 출력**: `true`

4. 다양한 값 테스트:
   - `true`, `false`, `1`, `0`, `yes`, `no` 등

### 확인 포인트

**@Value vs @ConfigurationProperties**:
- `@Value`: 개별 설정 값을 하나씩 주입 (간단한 경우)
- `@ConfigurationProperties`: 관련 설정을 객체로 묶어 주입 (복잡한 경우)

**SpEL 활용**:
```java
@Value("#{systemProperties['user.home']}")  // 시스템 속성
@Value("#{environment['PATH']}")  // 환경변수
@Value("#{T(java.lang.Math).random()}")  // 정적 메서드
```

### 추가 도전

**환경변수 직접 읽기**:
```java
@Value("${JAVA_HOME:not_set}")
private String javaHome;
```

**생성자 주입으로 @Value 사용**:
```java
@Component
public class MyService {
    private final String apiKey;

    public MyService(@Value("${api.key}") String apiKey) {
        this.apiKey = apiKey;
    }
}
```

---

## example13 — @ConfigurationProperties 설정 바인딩

### 학습 목표
- application.yml의 값을 Java 객체로 바인딩하는 방법
- 계층 구조, List, Map 바인딩
- @ConfigurationPropertiesScan의 역할

### 실행 방법
```
example13/Main.java 실행
```

### 핵심 개념
- `@ConfigurationProperties(prefix = "my.service")`: my.service.* 값을 필드에 자동 매핑
- Setter 기반 바인딩 (setter 메서드 필요)
- 타입 안전한 설정 관리

### 직접 해보기

#### 과제 1: 새로운 설정 클래스 만들기
**목표**: @ConfigurationProperties 직접 작성

1. example13 패키지에 `DatabaseProperties.java` 생성:
```java
package com.codeit.springbeanpractice.example13;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.database")
public class DatabaseProperties {
    private String url;
    private String username;
    private int maxConnections;

    // Getter, Setter 추가
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
}
```

2. `application.yml`에 설정 추가:
```yaml
my:
  database:
    url: jdbc:mysql://localhost:3306/mydb
    username: admin
    maxConnections: 50
```

3. Main에서 조회:
```java
DatabaseProperties db = context.getBean(DatabaseProperties.class);
System.out.println("DB URL: " + db.getUrl());
```

4. 실행하여 결과 확인
   - **기대 출력**: `DB URL: jdbc:mysql://localhost:3306/mydb`

#### 과제 2: List 바인딩
**목표**: application.yml의 리스트를 Java List로 매핑

1. DatabaseProperties에 추가:
```java
import java.util.List;

private List<String> allowedIps;

public List<String> getAllowedIps() {
    return allowedIps;
}

public void setAllowedIps(List<String> allowedIps) {
    this.allowedIps = allowedIps;
}
```

2. application.yml에 추가:
```yaml
my:
  database:
    url: jdbc:mysql://localhost:3306/mydb
    username: admin
    maxConnections: 50
    allowedIps:
      - 192.168.0.1
      - 192.168.0.2
      - 10.0.0.1
```

3. 출력 확인:
```java
System.out.println("허용된 IP: " + db.getAllowedIps());
```

4. 실행하여 결과 확인
   - **기대 출력**: `허용된 IP: [192.168.0.1, 192.168.0.2, 10.0.0.1]`

### 확인 포인트

**디버거로 확인**:
1. Main의 getBean() 줄에 브레이크포인트
2. 각 Properties 객체의 필드 값 확인
3. application.yml의 값과 정확히 매핑되는지 확인

---

## example14 — ApplicationContextInitializer로 PropertySource 추가

### 학습 목표
- 스프링 컨테이너 초기화 단계에서 설정 값을 코드로 추가하는 방법
- ApplicationContextInitializer의 역할과 사용법
- PropertySource 우선순위 이해

### 실행 방법
```
example14/AutoConfigurationSourceApplication.java 실행
```

### 핵심 개념
- `ApplicationContextInitializer`: 컨테이너 초기화 직전에 실행되는 훅(hook)
- `PropertySource`: 설정 값의 출처 (application.yml, 환경변수, 코드 등)
- `addFirst()`: 최우선 순위로 PropertySource 추가 (같은 키가 있으면 덮어씀)

### 직접 해보기

#### 과제 1: 코드로 추가한 설정 값 사용하기
**목표**: PropertySource에서 값을 읽어오는 방법 이해

1. `GreetingService.java` 확인 - `@Value("${custom.key}")` 사용 중

2. 실행하여 출력 확인
   - **기대 출력**: `custom.key = value-from-code`
   - application.yml에 없는 값도 코드로 추가하면 사용 가능

#### 과제 2: 우선순위 테스트
**목표**: addFirst()의 우선순위 동작 확인

1. application.yml에 동일한 키 추가:
```yaml
custom:
  key: value-from-yml
```

2. 실행
   - **기대 출력**: `custom.key = value-from-code`
   - **학습 포인트**: addFirst()로 추가한 PropertySource가 우선 적용됨

3. `AutoConfigurationSourceApplication.java`에서 addFirst를 addLast로 변경:
```java
env.getPropertySources().addLast(propertySource);  // addFirst → addLast
```

4. 실행
   - **기대 출력**: `custom.key = value-from-yml`
   - **학습 포인트**: addLast()는 우선순위가 낮아서 yml 값이 우선됨

5. 다시 addFirst로 복구

#### 과제 3: 여러 개의 설정 값 추가
**목표**: Map에 여러 값을 담아 PropertySource 생성

1. `initialize()` 메서드 수정:
```java
Map<String, Object> map = new HashMap<>();
map.put("custom.key", "value-from-code");
map.put("custom.name", "홍길동");
map.put("custom.age", "30");
```

2. GreetingService에 필드 추가:
```java
import org.springframework.beans.factory.annotation.Value;

@Value("${custom.name}")
private String name;

@Value("${custom.age}")
private int age;

public void printEnv() {
    System.out.println("custom.key = " + customKey);
    System.out.println("custom.name = " + name);
    System.out.println("custom.age = " + age);
}
```

3. 실행
   - **기대 출력**:
     ```
     custom.key = value-from-code
     custom.name = 홍길동
     custom.age = 30
     ```

#### 과제 4: 시스템 환경변수 가져오기
**목표**: Environment에서 시스템 설정 읽기

1. `initialize()` 메서드에 추가:
```java
String javaHome = env.getProperty("JAVA_HOME");
System.out.println("JAVA_HOME = " + javaHome);

String osName = env.getProperty("os.name");
System.out.println("OS Name = " + osName);
```

2. 실행
   - **기대 출력**: 시스템의 JAVA_HOME 경로와 OS 이름 출력

### 확인 포인트

**ApplicationContextInitializer 실제 활용 사례**:
- 외부 설정 서버(Config Server)에서 값을 가져와 Environment에 추가
- 암호화된 설정 값을 복호화하여 추가
- 환경별로 다른 설정 값을 동적으로 주입

**PropertySource 우선순위** (높은 것부터):
1. 코드로 addFirst()한 PropertySource
2. 커맨드 라인 인자
3. application.yml / application.properties
4. 시스템 환경변수
5. 코드로 addLast()한 PropertySource

### 추가 도전

**여러 개의 PropertySource 추가**:
```java
PropertySource<?> ps1 = new MapPropertySource("custom1", map1);
PropertySource<?> ps2 = new MapPropertySource("custom2", map2);
env.getPropertySources().addFirst(ps1);
env.getPropertySources().addFirst(ps2);
// ps2가 ps1보다 우선순위가 높음
```

---

## example15 — @Profile 기반 Bean 분기

### 학습 목표
- 환경별로 다른 Bean을 등록하는 방법
- Profile 활성화 방법
- 동일 인터페이스, 다른 Profile의 구현체 등록

### 핵심 개념
- `@Profile("dev")`: dev 프로파일일 때만 Bean 등록
- `application.yml`의 `spring.profiles.active`로 활성화
- 환경별(dev, test, prod) Bean 분기

### 직접 해보기

#### 과제 1: prod 프로파일 추가
**목표**: 운영 환경용 Bean 생성

1. example15 패키지에 `ProdLogger.java` 생성:
```java
package com.codeit.springbeanpractice.example15;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdLogger {
    @PostConstruct
    public void init() {
        System.out.println("[PROD] 운영 환경 로거 초기화");
    }
}
```

2. application.yml에서 active를 `prod`로 변경:
```yaml
spring:
  profiles:
    active:
      - prod
```

3. 실행
   - **기대 출력**: `[PROD] 운영 환경 로거 초기화`

#### 과제 2: 여러 Profile 동시 활성화
```yaml
spring:
  profiles:
    active:
      - dev
      - test
```
dev와 test Bean이 모두 등록됨

#### 과제 3: Profile 없는 Bean (@Profile 없음)
**목표**: 모든 환경에서 사용되는 공통 Bean

1. `CommonLogger.java` 생성:
```java
package com.codeit.springbeanpractice.example15;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component  // @Profile 없음
public class CommonLogger {
    @PostConstruct
    public void init() {
        System.out.println("[COMMON] 모든 환경에서 사용 가능");
    }
}
```

2. Profile을 어떻게 설정하든 항상 등록됨
   - **기대 출력**: Profile 설정과 무관하게 항상 `[COMMON] 모든 환경에서 사용 가능` 출력

---

## example16 — @Conditional + 커스텀 Condition

### 학습 목표
- Condition 인터페이스를 구현하여 조건부 Bean 등록
- Environment를 사용한 설정 값 기반 조건 판단
- @Conditional과 @Profile의 차이점 이해

### 실행 방법
```
example16/Main.java 실행
```

### 핵심 개념
- `@Conditional(조건클래스.class)`: 조건이 true일 때만 Bean 등록
- `Condition` 인터페이스: `matches()` 메서드를 구현하여 조건 로직 작성
- `ConditionContext`: Environment, BeanFactory 등에 접근 가능

### 직접 해보기

#### 과제 1: custom.feature-x를 true로 변경
**목표**: 조건이 만족될 때 Bean이 등록되는지 확인

1. `application.yml` 열기

2. custom.feature-x 값을 true로 변경:
```yaml
custom:
  feature-x: true
```

3. 실행
   - **기대 출력**: `FeatureService 등록됨`, `Feature X is enabled!` 메시지 출력

#### 과제 2: false로 변경하면?
**목표**: 조건이 만족되지 않을 때 Bean이 등록되지 않는지 확인

1. custom.feature-x를 false로 변경:
```yaml
custom:
  feature-x: false
```

2. 실행
   - **기대 결과**: `NoSuchBeanDefinitionException` 발생
   - **오류 메시지**: "No qualifying bean of type 'FeatureService' available"

#### 과제 3: 새로운 Condition 만들기
**목표**: 커스텀 조건 클래스를 직접 작성

1. `FeatureYEnabledCondition.java` 생성:
```java
package com.codeit.springbeanpractice.example16;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class FeatureYEnabledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("custom.feature-y");
        return Boolean.parseBoolean(property);
    }
}
```

2. application.yml에 설정 추가:
```yaml
custom:
  feature-y: true
```

3. 새로운 서비스 클래스 생성하여 테스트

### 확인 포인트

**디버거로 확인**:
1. `FeatureXEnabledCondition.matches()` 메서드에 브레이크포인트
2. `context.getEnvironment().getProperty("custom.feature-x")` 결과 확인
3. 조건이 false일 때는 matches가 호출되는지, Bean 생성은 어디서 막히는지 확인

**@Profile vs @Conditional**:
- `@Profile`: 환경(dev, test, prod) 기반 분기
- `@Conditional`: 임의의 조건 로직 작성 가능 (더 유연함)

---

## example17 — @DependsOn으로 Bean 생성 순서 제어

### 학습 목표
- Bean 생성 순서를 명시적으로 제어하는 방법
- @DependsOn의 동작 원리
- Profile과 결합하여 조건부 의존성 처리

### 실행 방법
```
example17/Main.java 실행
```

### 핵심 개념
- `@DependsOn({"beanA", "beanB"})`: 현재 Bean이 생성되기 전에 지정된 Bean들을 먼저 생성
- Bean 이름(빈 이름은 클래스명의 첫 글자를 소문자로)으로 의존성 지정
- Profile에 따라 의존하는 Bean이 없으면 오류 발생

### 직접 해보기

#### 과제 1: 생성 순서 확인
**목표**: 콘솔 출력으로 Bean 생성 순서 파악

1. 실행하여 출력 순서 확인
   - **기대 출력 순서**:
     ```
     SecondBean Constructor called
     BeanC Constructor called
     FirstBean Constructor called
     ```
   - FirstBean이 secondBean과 beanC에 의존하므로 가장 나중에 생성됨

#### 과제 2: @DependsOn 제거해보기
**목표**: @DependsOn이 없으면 순서가 보장되지 않음을 확인

1. `FirstBean.java`에서 `@DependsOn` 주석 처리:
```java
@Component
// @DependsOn({"secondBean", "beanC"})  // ← 주석 처리
public class FirstBean {
```

2. 여러 번 실행하여 순서가 달라지는지 확인
   - **기대 결과**: 실행마다 순서가 바뀔 수 있음

3. 다시 @DependsOn 복구

#### 과제 3: Profile과 결합
**목표**: 조건부 의존성 처리

1. `BeanC.java` 확인 - @Profile("test")로 설정되어 있음

2. application.yml에서 test 프로파일 제거:
```yaml
spring:
  profiles:
    active:
      - dev
      # - test  ← 주석 처리
```

3. 실행
   - **기대 결과**: `BeanCreationException` 발생
   - **오류 메시지**: "beanC" Bean을 찾을 수 없다는 내용
   - @DependsOn에 명시된 Bean이 없으면 애플리케이션 시작 실패

4. test 프로파일 다시 활성화

### 확인 포인트

**@DependsOn의 실제 용도**:
- 데이터베이스 초기화 Bean → 애플리케이션 Bean
- 캐시 초기화 Bean → 서비스 Bean
- 설정 검증 Bean → 비즈니스 로직 Bean

**주의사항**:
- @DependsOn은 생성 순서만 제어, 의존성 주입은 별개
- 순환 의존성 (@DependsOn 서로 참조) 하면 오류 발생

---

## example18 — CommandLineRunner + @Order로 초기화 로직 실행 순서 제어

### 학습 목표
- 애플리케이션 시작 후 초기화 로직을 실행하는 방법
- @Order와 Ordered 인터페이스로 실행 순서 제어
- CommandLineRunner의 실제 활용 사례 이해

### 실행 방법
```
example18/Main.java 실행
```

### 핵심 개념
- `CommandLineRunner`: Spring Boot 애플리케이션이 완전히 시작된 후 자동 실행되는 인터페이스
- `@Order(순서)` 또는 `Ordered` 인터페이스: 실행 순서 지정 (숫자가 작을수록 먼저 실행)
- 여러 개의 Runner가 있을 때 순서 제어 가능

### 직접 해보기

#### 과제 1: 실행 순서 확인
**목표**: Order 값에 따른 실행 순서 파악

1. 실행하여 출력 확인
   - **기대 출력**:
     ```
     SecondRunner 실행
     FirstRunner 실행
     ```
   - SecondRunner의 Order가 1, FirstRunner의 Order가 2이므로 SecondRunner가 먼저 실행

#### 과제 2: Order 값 변경
**목표**: Order 값과 실행 순서의 관계 이해

1. `FirstRunner.java`의 getOrder() 메서드 수정:
```java
@Override
public int getOrder() {
    return 0;  // 2 → 0으로 변경
}
```

2. 실행
   - **기대 출력**:
     ```
     FirstRunner 실행
     SecondRunner 실행
     ```
   - 순서가 바뀜

#### 과제 3: 새로운 Runner 추가
**목표**: 여러 개의 초기화 로직 작성

1. `ThirdRunner.java` 생성:
```java
package com.codeit.springbeanpractice.example18;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)  // 가장 먼저 실행
public class ThirdRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("ThirdRunner 실행 - 가장 먼저!");
    }
}
```

2. 실행하여 3개의 Runner가 순서대로 실행되는지 확인
   - **기대 출력**:
     ```
     ThirdRunner 실행 - 가장 먼저!
     SecondRunner 실행
     FirstRunner 실행
     ```

#### 과제 4: 실제 활용 예시 작성
**목표**: CommandLineRunner의 실용적인 사용법 이해

1. `DataInitRunner.java` 생성:
```java
package com.codeit.springbeanpractice.example18;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("[데이터 초기화] 기본 관리자 계정 생성");
        System.out.println("[데이터 초기화] 기본 설정 값 로딩");
        System.out.println("[데이터 초기화] 캐시 예열 완료");
    }
}
```

2. 실행
   - **기대 출력**: 초기화 메시지들이 순서대로 출력됨

### 확인 포인트

**CommandLineRunner 실제 활용 사례**:
- 데이터베이스 초기 데이터 삽입
- 캐시 예열 (warm-up)
- 외부 API 연결 확인
- 스케줄러 시작
- 파일 시스템 초기화

**ApplicationRunner와의 차이**:
- `CommandLineRunner`: `run(String... args)` - 문자열 배열로 인자 받음
- `ApplicationRunner`: `run(ApplicationArguments args)` - 더 풍부한 인자 파싱 제공

### 추가 도전

**조건부 실행**:
```java
@Component
@Order(1)
@ConditionalOnProperty(name = "app.init.enabled", havingValue = "true")
public class ConditionalRunner implements CommandLineRunner {
    // app.init.enabled=true 일 때만 실행
}
```

---

## example19 — 조건부 Bean + 커스텀 검증

### 학습 목표
- @Conditional로 조건에 따라 Bean 등록 제어
- 커스텀 Condition 구현
- @Validated + 커스텀 제약 조건 작성

### 핵심 개념
- `@Conditional(MyCondition.class)`: 조건이 true일 때만 Bean 등록
- `Condition` 인터페이스 구현으로 조건 로직 작성
- Profile + Condition 조합 가능

### 직접 해보기

#### 과제 1: 조건 변경하기
**목표**: DevAndTestProfileCondition 수정

1. `DevAndTestProfileCondition.java` 열기
2. 조건을 OR로 변경 (dev 또는 test):
```java
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Environment env = context.getEnvironment();
    boolean hasDev = env.acceptsProfiles(Profiles.of("dev"));
    boolean hasTest = env.acceptsProfiles(Profiles.of("test"));
    return hasDev || hasTest;  // AND → OR
}
```

3. application.yml에서 `test` 프로파일 제거:
```yaml
spring:
  profiles:
    active:
      - dev
      # - test 제거
```

4. 실행
   - **기대 결과**: dev만 있어도 Bean 등록됨
   - **기대 출력**: "DevAndTestLogger Bean이 조건을 만족하여 정상 등록되었습니다."

#### 과제 2: 환경 변수 기반 조건 만들기
**목표**: 시스템 환경 변수로 Bean 등록 제어

1. example19/condition 패키지에 `EnvBasedCondition.java` 생성:
```java
package com.codeit.springbeanpractice.example19.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EnvBasedCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String env = System.getenv("MY_APP_ENV");
        return "production".equals(env);
    }
}
```

2. Bean에 적용:
```java
package com.codeit.springbeanpractice.example19.condition;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(EnvBasedCondition.class)
public class ProductionOnlyBean {
}
```

3. 환경 변수 설정 후 실행

---

## 자주 하는 실수

### 1. Bean 이름 오타
```java
context.getBean("getMember");  // 정상: 메서드 이름과 정확히 일치
context.getBean("getMember1");  // 오류: 오타 → NoSuchBeanDefinitionException
```

### 2. @ComponentScan 범위 실수
```java
@ComponentScan(basePackages = "com.codeit.springbeanpractice.example03")  // 정상
@ComponentScan(basePackages = "com.codeit.example03")  // 오류: 패키지 경로 오류
```

### 3. 순환 참조
```java
// 절대 하지 마세요
@Service
public class AService {
    private final BService bService;  // A → B
}

@Service
public class BService {
    private final AService aService;  // B → A (순환!)
}
```

### 4. proxyMode 누락
```java
// 오류: 싱글톤에 session 스코프를 직접 주입
@Scope(scopeName = "session")  // proxyMode 없음 → 오류!

// 정상: 프록시 사용
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
```

---

## 추가 학습 리소스

### Spring 공식 문서
- [Core Technologies](https://docs.spring.io/spring-framework/reference/core.html)
- [IoC Container](https://docs.spring.io/spring-framework/reference/core/beans.html)

### 디버거 활용
- Bean의 실제 타입 확인 (프록시 여부)
- beanDefinitionMap에서 등록된 모든 Bean 탐색
- 싱글톤 여부 확인 (== 비교)

### IntelliJ 단축키
- `Ctrl + Shift + N` (Windows/Linux) / `Cmd + Shift + O` (Mac): 클래스 찾기
- `Ctrl + B` (Windows/Linux) / `Cmd + B` (Mac): Bean 정의로 이동
- `F8`: 한 줄씩 실행 (디버깅)
- `F9`: 다음 브레이크포인트까지 실행

---

## 도움이 필요할 때

막혔을 때 확인할 것:
1. 예외 메시지 끝까지 정확히 읽기
2. Bean 이름 오타 확인
3. @ComponentScan 범위 확인
4. application.yml의 들여쓰기 확인 (YAML은 들여쓰기 민감)
5. Profile이 올바르게 활성화되었는지 확인

**디버거를 적극 활용하세요!** 코드를 읽는 것보다 실행하면서 보는 것이 100배 효과적입니다.

---

**Happy Learning!**
