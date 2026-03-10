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
