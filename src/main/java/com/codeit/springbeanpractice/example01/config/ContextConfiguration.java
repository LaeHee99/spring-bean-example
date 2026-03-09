package com.codeit.springbeanpractice.example01.config;

import com.codeit.springbeanpractice.example01.dto.MemberDTO;
import com.codeit.springbeanpractice.example01.entity.Account;
import com.codeit.springbeanpractice.example01.entity.PersonalAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("configurationSection02")
// 설정 클래스(Configuration)를 스프링 빈으로 등록
// - 빈 이름을 "configurationSection02"로 지정
public class ContextConfiguration {

//    @Bean(name = "member")
    // @Bean에 name을 지정하면 빈 이름을 명시적으로 설정 가능
    // 지정하지 않으면 메서드 이름(getMember)이 빈 이름이 됨

    @Bean
    public MemberDTO getMember() {
        // MemberDTO 타입의 스프링 빈 생성
        return new MemberDTO(
                1,
                "010-1234-5678",
                "jungmin@google.com",
                "이정민"
        );
    }

    // 과제 2: @Bean의 name 속성으로 이름 변경
    @Bean(name = "mySpecialAccount")
    public Account createAccount() {
        return new PersonalAccount("999-888-777", 5000000, 101);
    }
}
