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

## 권장 학습 순서

### Day 1 - Bean 등록 기초
- **example01-02**: @Configuration + @Bean
- **example03-05**: @ComponentScan
- **example06-07**: 생성자 주입 + 계층 구조

### Day 2 - 의존성 주입 심화
- **example08-09**: 동일 타입 Bean 여러 개 처리
- **example10**: 스코프와 프록시
- **example11-12**: 생명주기 + @Value

### Day 3 - 설정과 조건부 Bean
- **example13-14**: @ConfigurationProperties
- **example15-16**: @Profile + @Conditional
- **example17-19**: Bean 순서 + 통합 패턴

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

5. Main 클래스에서 조회:
```java
Account account = context.getBean("createAccount", Account.class);
System.out.println("account = " + account);
```

6. 실행하여 결과 확인
   - **기대 출력**: `account = ...` 형태로 Account 객체 정보 출력
   - **확인 사항**: Bean 이름이 메서드 이름(`createAccount`)과 동일

#### 과제 2: Bean 이름 변경하기
**목표**: @Bean의 name 속성 이해

1. `ContextConfiguration.java`에서 `@Bean(name = "mySpecialAccount")` 로 변경
2. Main에서 조회 시 이름을 `"mySpecialAccount"`로 변경
3. 실행하여 정상 동작 확인
   - **기대 출력**: 동일하게 Account 객체 정보 출력
   - **학습 포인트**: Bean 이름이 메서드 이름이 아니라 name 속성 값이 됨

#### 과제 3: Bean을 못 찾는 오류 만들어보기
**목표**: NoSuchBeanDefinitionException 이해

1. Main 클래스에서 존재하지 않는 Bean 조회:
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

1. Main 클래스 수정:
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

1. Main 클래스에서:
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

2. @ComponentScan의 basePackages에 추가:
```java
@ComponentScan(basePackages = {
    "com.codeit.springbeanpractice.example03.service",
    "com.codeit.springbeanpractice.example03.repository"
    // controller도 추가하려면 여기에 추가
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

1. import 추가:
```java
import com.codeit.springbeanpractice.example04.repository.MemberRepository;
```

2. FilterType을 ASSIGNABLE_TYPE으로 변경:
```java
@ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = MemberRepository.class
)
```

3. 실행하여 MemberRepository만 정확히 제외되는지 확인
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

3. `Main.java`에서 테스트:
```java
MemberService service = context.getBean(MemberService.class);
service.registerMember();
```

4. 실행하여 "회원 저장됨!" 메시지 확인
   - **기대 출력**: `회원 저장됨!`

#### 과제 2: 필드 주입으로 변경해보기 (비권장 방식 체험)
**목표**: 생성자 주입과 필드 주입 비교

1. Service를 필드 주입으로 변경:
```java
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class MemberController {
    @Autowired  // ← 생략 불가
    private MemberService memberService;  // ← final 사용 불가
}
```

**차이점 정리**:
| 구분 | 생성자 주입 | 필드 주입 |
|------|-------------|-----------|
| @Autowired | 생략 가능 | 생략 불가 |
| final | 사용 가능 | 사용 불가 |
| 불변성 | 보장됨 | 보장 안 됨 |
| 테스트 | 쉬움 | 어려움 |

#### 과제 3: Setter 주입도 시도해보기
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

4. Main에서 테스트:
```java
bookService.addBook(new Book(4, "테스트 도서", "홍길동"));
```

5. 실행하여 결과 확인
   - **기대 출력**: `책 저장됨: Book{...}`

#### 과제 3: 책 삭제 기능도 추가
**목표**: 스스로 CRUD 메서드 완성

1. `void deleteBySequence(int sequence)` 메서드 추가
2. 인터페이스 → 구현체 → 서비스 → Main 순서로 작성
3. 존재하지 않는 책을 삭제하려고 하면 예외 발생하도록 구현

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

2. PokemonServiceV2 실행하여 Map에 4개 포켓몬이 모두 출력되는지 확인
   - **기대 출력**: charmander, pikachu, squirtle, bulbasaur 모두 출력

#### 과제 4: Set으로 주입해보기
**목표**: Set<Pokemon> 주입 방식 학습

1. PokemonServiceV2에 필드와 생성자 추가:
```java
private final Set<Pokemon> pokemonSet;

public PokemonServiceV2(Set<Pokemon> pokemonSet) {
    this.pokemonSet = pokemonSet;
}

public void attackAll() {
    pokemonSet.forEach(Pokemon::attack);
}
```

2. Main에서 호출하여 모든 포켓몬 공격 확인

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

## example10 — session scope + scoped proxy

### 학습 목표
- Bean 스코프의 종류와 차이점 이해
- session 스코프의 동작 원리
- Scoped Proxy가 필요한 이유

### 핵심 개념
- **singleton** (기본값): 애플리케이션 전체에서 1개 인스턴스
- **prototype**: 요청할 때마다 새로운 인스턴스 생성
- **request**: HTTP 요청당 1개 인스턴스
- **session**: HTTP 세션당 1개 인스턴스
- **proxyMode**: 싱글톤 Bean에 session/request 스코프 Bean을 주입하기 위해 필요

### 직접 해보기

#### 과제 1: prototype 스코프로 변경
**목표**: 매번 새로운 인스턴스가 생성되는지 확인

1. `SessionScopeBean.java` 수정:
```java
@Component
@Scope(scopeName = "prototype")  // session → prototype
public class SessionScopeBean {
}
```

2. Main 클래스 수정:
```java
SessionScopeBean bean1 = context.getBean(SessionScopeBean.class);
SessionScopeBean bean2 = context.getBean(SessionScopeBean.class);
System.out.println("같은 객체? " + (bean1 == bean2));
```

3. 실행
   - **기대 출력**: `같은 객체? false` (매번 새 인스턴스 생성)

#### 과제 2: proxyMode 제거하면?
**목표**: 프록시의 필요성 이해

1. SessionScopeBean에서 `proxyMode` 제거:
```java
@Scope(scopeName = "session")  // proxyMode 제거
```

2. 실행
   - **기대 결과**: 오류 발생
   - **오류 메시지**: 싱글톤 Bean에 session 스코프 Bean을 직접 주입할 수 없다는 내용

**왜 오류가 발생할까?**
- OrderService는 싱글톤 (애플리케이션 시작 시 1번 생성)
- SessionScopeBean은 세션마다 다른 인스턴스
- 싱글톤에 세션 스코프 Bean을 직접 주입하면 세션이 바뀌어도 같은 Bean 사용
- **proxyMode**를 사용하면 프록시 객체를 주입하고, 실제 호출 시점에 세션에 맞는 Bean 연결

3. 다시 proxyMode 복구

#### 과제 3: request 스코프로 변경
```java
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
```
HTTP 요청마다 새로운 인스턴스 생성

### 추가 도전

**프록시 클래스 확인하기**:
```java
SessionScopeBean bean = context.getBean(SessionScopeBean.class);
System.out.println(bean.getClass().getName());
```
- **기대 출력**: `SessionScopeBean$$EnhancerBySpringCGLIB$$...` 형태

---

## example13 — @ConfigurationProperties 설정 바인딩

### 학습 목표
- application.yml의 값을 Java 객체로 바인딩하는 방법
- 계층 구조, List, Map 바인딩
- @ConfigurationPropertiesScan의 역할

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

## 전체 학습 체크리스트

완료한 항목에 체크하세요!

### Day 1 - Bean 등록 기초
- [ ] example01: Bean을 추가/삭제하고 이름으로 조회해봤는가?
- [ ] example02: 싱글톤을 직접 확인하고 @Component와 차이를 실험했는가?
- [ ] example03: 스캔 범위를 바꿔보고 Bean이 누락되는 경우를 경험했는가?
- [ ] example04: excludeFilters로 Bean을 제외하고 오류를 확인했는가?
- [ ] example06-07: 3계층 구조를 직접 만들어봤는가?

### Day 2 - 의존성 주입 심화
- [ ] example08: @Primary, @Qualifier, Map 주입을 모두 시도해봤는가?
- [ ] example08: 새로운 Pokemon을 추가하고 주입 받았는가?
- [ ] example09: Optional 주입을 테스트해봤는가?
- [ ] example10: prototype과 session 스코프의 차이를 확인했는가?
- [ ] example10: proxyMode를 제거하고 오류를 경험했는가?

### Day 3 - 설정과 조건부 Bean
- [ ] example13: 새로운 @ConfigurationProperties 클래스를 만들었는가?
- [ ] example13: List, Map 바인딩을 직접 작성했는가?
- [ ] example15: dev/test/prod 환경을 바꿔가며 테스트했는가?
- [ ] example19: 조건을 OR로 바꾸고 동작을 확인했는가?

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
