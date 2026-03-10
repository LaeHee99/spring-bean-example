package com.codeit.springbeanpractice.example19.condition;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * 환경 변수 MY_APP_ENV=production 일 때만 등록되는 Bean
 *
 * 사용법:
 * - 환경 변수 설정 없이 실행: Bean 등록 안 됨
 * - export MY_APP_ENV=production 후 실행: Bean 등록됨
 */
@Component
@Conditional(EnvBasedCondition.class)
public class ProductionOnlyBean {

    public ProductionOnlyBean() {
        System.out.println("🚀 ProductionOnlyBean 생성! (환경 변수 MY_APP_ENV=production)");
    }

    public void doSomething() {
        System.out.println("운영 환경 전용 기능 실행!");
    }
}
