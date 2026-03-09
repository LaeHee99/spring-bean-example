package com.codeit.springbeanpractice.example01;

import com.codeit.springbeanpractice.example01.config.ContextConfiguration;
import com.codeit.springbeanpractice.example01.dto.MemberDTO;
import com.codeit.springbeanpractice.example01.entity.Account;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication // 스프링 부트 설정 클래스 (이번 예제에서는 run 사용 안 함)
public class SpringBeanPracticeApplication {

    public static void main(String[] args) {

//        SpringApplication.run(SpringBeanPracticeApplication.class, args);
        // ❌ 스프링 부트 자동 실행 방식
        // → 이번 예제에서는 사용하지 않음

        // @Configuration 클래스(ContextConfiguration)를 기반으로
        // 스프링 컨테이너를 직접 생성
        ApplicationContext context =
                new AnnotationConfigApplicationContext(ContextConfiguration.class);

        // @Bean 메서드(getMember)로 등록된 MemberDTO 빈 조회
        MemberDTO member =
                context.getBean("getMember", MemberDTO.class);
        System.out.println("member = " + member);

        // 과제 2: name 속성으로 변경한 Bean 이름으로 조회
        Account account = context.getBean("mySpecialAccount", Account.class);
        System.out.println("account = " + account);

        // 과제 3: 존재하지 않는 Bean 조회 시도 (오류 발생 예상)
        try {
            context.getBean("nonExistentBean");
            System.out.println("❌ 오류가 발생하지 않았습니다!");
        } catch (Exception e) {
            System.out.println("✅ 예상대로 오류 발생: " + e.getClass().getSimpleName());
            System.out.println("   메시지: " + e.getMessage());
        }

        // configurationSection02라는 이름의 빈 존재 여부 확인용 조회
        // (해당 이름의 @Bean 또는 @Configuration이 등록돼 있어야 함)
        context.getBean("configurationSection02");
    }
}
