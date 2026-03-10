package com.codeit.springbeanpractice.example03;

// import를 안 해도 ComponentScan에는 영향 없음
// (스캔은 클래스패스 + 애노테이션 기준)
//import com.codeit.springbeanpractice.example03.repository.MemberRepository;
//import com.codeit.springbeanpractice.example03.service.MemberService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.codeit.springbeanpractice.example03.service",
                "com.codeit.springbeanpractice.example03.repository"
        }
)
// 지정한 패키지만 컴포넌트 스캔
// → 해당 패키지 하위의 @Service, @Repository, @Component 만 빈 등록

//@ComponentScan(basePackageClasses = {MemberService.class, MemberRepository.class})
// 위 방식은 클래스 기준으로 스캔 범위를 지정하는 대안
// (클래스가 속한 패키지를 기준으로 스캔)
public class SpringBeanPracticeApplication {

    public static void main(String[] args) {

        // 스프링 애플리케이션 실행 + 컨테이너 생성
        ApplicationContext context =
                SpringApplication.run(SpringBeanPracticeApplication.class, args);

        // MemberRepository 빈 조회
        // - repository 패키지가 ComponentScan 범위에 포함되어 있어야 정상 동작
        Object memberRepository = context.getBean("memberRepository");
        System.out.println("✅ memberRepository Bean 조회 성공: " + memberRepository.getClass().getSimpleName());

        Object memberService = context.getBean("memberService");
        System.out.println("✅ memberService Bean 조회 성공: " + memberService.getClass().getSimpleName());
    }
}
