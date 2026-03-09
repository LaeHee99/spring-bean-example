package com.codeit.springbeanpractice.example19;

import com.codeit.springbeanpractice.example19.condition.DevAndTestLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

// example19의 실행 진입점
// @Conditional + Profile 기반 Bean 등록 동작을 확인하기 위한 메인 클래스
@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        System.out.println("=== example19: 조건부 Bean + 커스텀 검증 ===\n");

        // 스프링 컨테이너 초기화
        // 이 시점에:
        // - 활성화된 profile 확인 (application.yml에서 dev, test 활성화)
        // - @Conditional 조건 평가
        // - 조건에 맞는 Bean만 등록됨
        ApplicationContext context =
                SpringApplication.run(Main.class, args);

        // DevAndTestLogger Bean 조회
        // → dev AND test 프로파일이 모두 활성화된 경우에만 Bean이 존재
        // → application.yml에서 dev, test 둘 다 활성화했으므로 정상 조회됨
        // → 만약 조건이 맞지 않으면 NoSuchBeanDefinitionException 발생
        try {
            DevAndTestLogger logger = context.getBean(DevAndTestLogger.class);
            System.out.println("\n✅ DevAndTestLogger Bean이 조건을 만족하여 정상 등록되었습니다.");
            System.out.println("   (dev AND test 프로파일이 모두 활성화됨)");
        } catch (Exception e) {
            System.out.println("\n❌ DevAndTestLogger Bean이 등록되지 않았습니다.");
            System.out.println("   → dev와 test 프로파일이 모두 활성화되지 않았기 때문입니다.");
            System.out.println("   → application.yml에서 spring.profiles.active를 확인하세요.");
        }
    }
}
