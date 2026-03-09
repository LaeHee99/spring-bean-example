package com.codeit.springbeanpractice.example06;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

// example06: Controller → Service 생성자 주입 예제
@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        System.out.println("=== example06: Controller → Service 생성자 주입 ===\n");

        // 스프링 애플리케이션 실행 + ApplicationContext 생성
        ApplicationContext context =
                SpringApplication.run(Main.class, args);

        // MemberController와 MemberService Bean이 정상 등록되었는지 확인
        MemberController controller = context.getBean(MemberController.class);
        MemberService service = context.getBean(MemberService.class);

        System.out.println("✅ MemberController Bean 등록됨: " + controller.getClass().getSimpleName());
        System.out.println("✅ MemberService Bean 등록됨: " + service.getClass().getSimpleName());
        System.out.println("\n💡 MemberController에 MemberService가 생성자 주입되었습니다.");
        System.out.println("   디버거로 controller 객체의 memberService 필드를 확인해보세요!");
    }
}
