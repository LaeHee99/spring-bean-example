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
