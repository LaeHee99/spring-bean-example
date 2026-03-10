package com.codeit.springbeanpractice.example19.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 환경 변수 기반 조건부 Bean 등록
 *
 * MY_APP_ENV 환경 변수가 "production"일 때만 Bean 등록
 */
public class EnvBasedCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 시스템 환경 변수에서 MY_APP_ENV 값을 가져옴
        String env = System.getenv("MY_APP_ENV");

        // 환경 변수 값이 "production"일 때만 true 반환 (Bean 등록)
        return "production".equals(env);
    }
}
