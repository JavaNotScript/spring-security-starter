package com.gicovi.security.config;

import com.gicovi.security.properties.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityStarterAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.enabled=true");
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.enabled=false");
    private final WebApplicationContextRunner webApplicationContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class);
    private final WebApplicationContextRunner getContextRunner = new WebApplicationContextRunner()
            .withUserConfiguration(SecurityStarterAutoConfiguration.class)
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.public-endpoints-list[0]=/api/login",
                    "gicovi.security.public-endpoints-list[1]=/api/register");

    @Test
    void securityFilterIsCreatedWhenEnabled() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SecurityFilterChain.class);
                }
        );
    }

    @Test
    void securityFilterChainWhenDisabled() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SecurityFilterChain.class);
        });

    }

    @Test
    void securityFilterChainWhenEnabledPropertyIsMissing() {
        webApplicationContextRunner
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SecurityFilterChain.class);
                });
    }

    @Test
    void securityFilterChainCreatedWithPublicEndpoints() {
        contextRunner.withPropertyValues("gicovi.security.public-endpoints-list[0]=/api/login",
                        "gicovi.security.public-endpoints-list[1]=/api/register")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context).hasSingleBean(SecurityFilterChain.class);

                            context.getBeansOfType(SecurityProperties.class).forEach((name, bean) ->
                                    System.out.println(name + " -> " + bean.getPublicEndpointsList()));
                        }
                );
    }

    @Test
    void endpointsMatch() {
        getContextRunner.run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            SecurityProperties securityProperties = context.getBean(SecurityProperties.class);

                            assertThat(securityProperties.getPublicEndpointsList())
                                    .containsExactly("/api/login","/api/register");
                        }
                );
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
    }
}


