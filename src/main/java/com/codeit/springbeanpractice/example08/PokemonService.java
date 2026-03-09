package com.codeit.springbeanpractice.example08;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("pokemonServiceQualifier") // 서비스 빈 등록 + 빈 이름 지정
public class PokemonService {

    // ❌ 순환 참조 방지를 위해 PokemonServiceV2 의존성 제거
    // private PokemonServiceV2 pokemonServiceV2;

    private Pokemon pokemon;  // Pokemon 인터페이스 타입 의존성

    // 생성자 주입 - @Qualifier로 특정 구현체 지정
    // - Pokemon 구현체가 여러 개일 경우(@Component 여러 개),
    //   @Primary 또는 @Qualifier가 없으면 주입 시 에러 발생
    // - 여기서는 @Qualifier로 "pikachu" 구현체를 명시적으로 선택
    public PokemonService(@Qualifier("pikachu") Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    // ===== 다른 주입 방식 예시들 =====

    // 방법 1: @Resource(name = "pikachu")
    // - setter 주입 방식
    // - 이름 기준으로 빈을 주입
    // - JSR-250 표준 애노테이션
//    @Resource(name = "pikachu")
//    public void setPokemon(Pokemon pokemon) {
//        this.pokemon = pokemon;
//    }

    // 방법 2: 생성자만 사용 (Pikachu에 @Primary 있을 때)
    // - @Primary가 붙은 구현체가 자동 선택됨
//    public PokemonService(Pokemon pokemon) {
//        this.pokemon = pokemon;
//    }

    // 방법 3: 생성자 + @Qualifier로 다른 구현체 선택
//    public PokemonService(@Qualifier("squirtle") Pokemon pokemon) {
//        this.pokemon = pokemon;
//    }

    public void pokemonAttack() {
        pokemon.attack(); // 주입된 Pokemon 구현체의 공격 실행
    }
}
