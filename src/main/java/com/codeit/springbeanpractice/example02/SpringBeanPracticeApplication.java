package com.codeit.springbeanpractice.example02;

import com.codeit.springbeanpractice.example01.entity.Account;
import com.codeit.springbeanpractice.example02.config.ContextConfiguration;
import com.codeit.springbeanpractice.example02.dto.MemberDTO;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

@SpringBootApplication // 스프링 부트 설정 클래스 (이번 예제에서는 run 사용 안 함)
public class SpringBeanPracticeApplication {

    public static void main(String[] args) {

//        SpringApplication.run(SpringBeanPracticeApplication.class, args);
        // ❌ 스프링 부트 자동 실행 방식
        // → 이번 예제에서는 사용하지 않음

        // @Configuration 클래스(ContextConfiguration) 기반으로
        // 스프링 컨테이너를 직접 생성
        ApplicationContext context =
                new AnnotationConfigApplicationContext(ContextConfiguration.class);

        // 과제 2: Account Bean 싱글톤 확인
        System.out.println("=== 과제 2: Account 싱글톤 확인 ===");

        MemberDTO member = context.getBean("getMember", MemberDTO.class);
        Account account1 = member.getPersonalAccount();  // MemberDTO 안의 Account
        System.out.println("MemberDTO 안의 Account: " + account1);

        Account account2 = context.getBean("accountGenerator", Account.class);  // 직접 조회
        System.out.println("직접 조회한 Account: " + account2);

        System.out.println("같은 Account 객체인가? " + (account1 == account2));
        System.out.println("예상: true (프록시가 싱글톤 보장)");

//        // 스프링 컨테이너에 등록된 모든 빈 이름 출력
//        Arrays.stream(context.getBeanDefinitionNames())
//                .forEach(System.out::println);
    }
}
