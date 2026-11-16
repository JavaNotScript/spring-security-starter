package com.gicovi.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityStarterAutoConfigurationTest {

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
    }

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.enabled=true");

    @Test
    void securityFilterIsCreated() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SecurityFilterChain.class);
                }
        );
    }

    @Test
    void securityFilterChainDisabled() {
        new WebApplicationContextRunner()
                .withPropertyValues("gicovi.security.enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SecurityFilterChain.class);
                });

    }



}

