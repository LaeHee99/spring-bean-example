package com.codeit.springbeanpractice.example15;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component  // @Profile 없음
public class CommonLogger {
    @PostConstruct
    public void init() {
        System.out.println("[COMMON] 모든 환경에서 사용 가능");
    }
}
