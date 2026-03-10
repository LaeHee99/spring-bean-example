package com.codeit.springbeanpractice.example18;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
// 애플리케이션 시작 후 자동 실행되는 CommandLineRunner Bean
//@Order(2)
// @Order 애노테이션으로 실행 순서를 지정할 수도 있음
public class FirstRunner implements CommandLineRunner, Ordered {

    @Override
    public void run(String... args) throws Exception {
        // Spring Boot 애플리케이션이 완전히 기동된 후 실행됨
        System.out.println("FirstRunner 실행");
    }

    @Override
    public int getOrder() {
        // Ordered 인터페이스를 통해 실행 순서를 지정
        // 숫자가 작을수록 먼저 실행됨
        return 2;  // 원래대로 복원
    }
}
