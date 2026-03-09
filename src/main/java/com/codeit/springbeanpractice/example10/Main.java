package com.codeit.springbeanpractice.example10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // 스프링 부트 애플리케이션 시작 클래스
public class Main {

    public static void main(String[] args) {

        // 스프링 애플리케이션 실행 + 스프링 컨테이너(ApplicationContext) 생성
        ApplicationContext context =
                SpringApplication.run(Main.class, args);

        System.out.println("=== example10: session scope + scoped proxy ===\n");

        // 스프링 컨테이너에 등록된 OrderService 빈 조회
        OrderService orderService =
                context.getBean(OrderService.class);

        // OrderService의 메서드 호출
        // ⚠️ 주의: 이 예제는 웹 애플리케이션 환경에서 제대로 동작합니다
        // - 현재는 main() 메서드에서 직접 실행하므로 HTTP 세션이 없음
        // - 실제 세션 스코프 동작을 확인하려면:
        //   1) Controller를 만들어서 HTTP 요청으로 테스트하거나
        //   2) 디버거로 orderService의 sessionScopeBean 필드를 확인
        //      → 실제 객체가 아니라 프록시(CGLIB)가 주입된 것을 확인할 수 있음
        orderService.hello();

        System.out.println("\n✅ OrderService에는 SessionScopeBean의 프록시가 주입되어 있습니다.");
        System.out.println("   실제 세션 스코프 빈은 HTTP 요청이 있을 때 생성됩니다.");
    }
}
