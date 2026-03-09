package com.codeit.springbeanpractice.example08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // 스프링 부트 애플리케이션 시작점
public class PokemonApplication {

    public static void main(String[] args) {

        // 스프링 애플리케이션 실행 + ApplicationContext 생성
        ApplicationContext context =
                SpringApplication.run(PokemonApplication.class, args);

        System.out.println("=== example08: 동일 타입 Bean 여러 개 주입 ===\n");

        // 1. PokemonService: @Qualifier로 특정 구현체 주입
        System.out.println("1. PokemonService (@Qualifier 사용):");
        PokemonService service = context.getBean(PokemonService.class);
        service.pokemonAttack();

        // 2. PokemonServiceV2: Map으로 모든 Pokemon 구현체 주입
        System.out.println("\n2. PokemonServiceV2 (Map<String, Pokemon> 주입):");
        PokemonServiceV2 serviceV2 = context.getBean(PokemonServiceV2.class);
        serviceV2.pokemonAttack();
    }
}
