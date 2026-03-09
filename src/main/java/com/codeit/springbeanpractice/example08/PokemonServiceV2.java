package com.codeit.springbeanpractice.example08;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service // 스프링이 이 클래스를 서비스 빈으로 등록
public class PokemonServiceV2 {

    // ❌ 순환 참조 방지를 위해 PokemonService 의존성 제거
    // private final PokemonService pokemonService;

    private final Map<String, Pokemon> pokemons;
    // Pokemon 타입 빈들을 Map으로 주입
    // - key   : 빈 이름 (예: "charmander", "pikachu")
    // - value : Pokemon 구현체

    // 생성자 주입
    // - Pokemon 구현체 전체를 Map 형태로 주입
    public PokemonServiceV2(Map<String, Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    // 다른 주입 방식 예시들
    // --------------------------------------------------

    // @Resource
    // private Set<Pokemon> pokemons;
    // → 타입 기준으로 모든 Pokemon 구현체 주입

    // public PokemonServiceV2(Set<Pokemon> pokemons) {
    //     this.pokemons = pokemons;
    // }

    // @Autowired
    // public PokemonServiceV2(Map<String, Pokemon> pokemons) {
    //     this.pokemons = pokemons;
    // }

    // --------------------------------------------------

    public void pokemonAttack() {
        // Map에 들어있는 모든 Pokemon 빈 순회
        // key = 빈 이름, value = 실제 Pokemon 구현체
        pokemons.forEach((k, v) -> {
            System.out.println("Key: " + k + " Value: " + v);
            v.attack();
        });
    }
}
