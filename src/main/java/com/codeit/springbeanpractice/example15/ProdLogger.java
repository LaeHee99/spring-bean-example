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
