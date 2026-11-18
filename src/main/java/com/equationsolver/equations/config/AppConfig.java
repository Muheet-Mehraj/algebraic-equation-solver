package com.equationsolver.equations.config;

import com.equationsolver.equations.core.EquationParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EquationParser equationParser() {
        return new EquationParser();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Algebraic Equation Store & Solver API")
                .version("1.0.0")
                .description("Stores algebraic equations as postfix (expression) trees, lists them back "
                        + "as infix text, and evaluates them for given variable values."));
    }
}
