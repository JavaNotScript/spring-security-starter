package com.gicovi.security.config;

import com.gicovi.security.properties.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityStarterAutoConfigurationTest {

    private final WebApplicationContextRunner enabledRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.enabled=true");

    private final WebApplicationContextRunner defaultRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class);


    private final WebApplicationContextRunner disabledRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecurityConfig.class)
            .withPropertyValues("gicovi.security.enabled=false");

    @Test
    void securityFilterIsCreatedWhenEnabled() {
        enabledRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SecurityFilterChain.class);
                }
        );
    }

    @Test
    void securityFilterChainWhenDisabled() {
        disabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(SecurityFilterChain.class);
        });
    }

    @Test
    void securityFilterChainWhenEnabledPropertyIsMissing() {
        defaultRunner
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(SecurityFilterChain.class);
                });
    }

    @Test
    void publicEndpointsBoundWhenEnabledPropertyMissing() {
        enabledRunner.withPropertyValues("gicovi.security.public-endpoints[0]=/api/login",
                        "gicovi.security.public-endpoints[1]=/api/register")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context).hasSingleBean(SecurityFilterChain.class);

                            SecurityProperties securityProperties = context.getBean(SecurityProperties.class);

                            assertThat(securityProperties.getPublicEndpoints())
                                    .containsExactly("/api/login","/api/register");
                        }
                );
    }

    @Test
    void endpointsMatch() {
        defaultRunner.withPropertyValues("gicovi.security.public-endPoints[0]=/api/login"
                        ,"gicovi.security.public-endPoints[1]=/api/register")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            SecurityProperties securityProperties = context.getBean(SecurityProperties.class);

                            assertThat(securityProperties.getPublicEndpoints())
                                    .containsExactly("/api/login","/api/register");
                        }
                );
    }

    @Test
    void starterBacksOffWhenConsumerDefinesOneFilterChain() {
        defaultRunner
                .withUserConfiguration(OneFilterChainConsumerConfig.class)
                .run(
                context -> {
                    assertThat(context).hasNotFailed();

                    assertThat(context.getBeansOfType(SecurityFilterChain.class))
                            .hasSize(1)
                            .containsKey("consumerFilterChain");


                }
        );
    }

    @Test
    void starterBacksOffWhenConsumerDefinesTwoFilterChain() {
        defaultRunner.withUserConfiguration(TwoFilterChainConsumerConfig.class)
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();

                            assertThat(context.getBeansOfType(SecurityFilterChain.class))
                                    .hasSize(2)
                                    .containsKeys("securityFilterChainOne","securityFilterChainTwo");
                        }
                );
    }

    @Test
    void starterCreatesDefaultChainWhenNoConsumerExists() {
        defaultRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBeansOfType(SecurityFilterChain.class)).hasSize(1);
                    assertThat(context).hasBean("securityFilterChain");
        });
    }

    @Test
    void consumerChainIdentityPreserved() {
        defaultRunner.withUserConfiguration(OneFilterChainConsumerConfig.class)
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();

                            SecurityFilterChain filterChain = context.getBean("consumerFilterChain", SecurityFilterChain.class);
                            assertThat(filterChain).isSameAs(OneFilterChainConsumerConfig.SECURITY_FILTER_CHAIN_ATOMIC_REFERENCE.get());
                        }
                );
    }


    //provide httpSecurity
    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
    }

    @Configuration(proxyBeanMethods = false)
    static class OneFilterChainConsumerConfig{

        static final AtomicReference<SecurityFilterChain> SECURITY_FILTER_CHAIN_ATOMIC_REFERENCE = new AtomicReference<>();


        @Bean
        SecurityFilterChain consumerFilterChain(HttpSecurity security){
            SecurityFilterChain chain = security
                    .securityMatcher("/consumer/**")
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .build();
            SECURITY_FILTER_CHAIN_ATOMIC_REFERENCE.set(chain);

            return chain;
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TwoFilterChainConsumerConfig{


        @Bean
        SecurityFilterChain securityFilterChainOne(HttpSecurity security){
            return security
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                    .build();
        }

        @Bean
        SecurityFilterChain securityFilterChainTwo(HttpSecurity security){
            return security
                    .securityMatcher("/admin/**")
                    .authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
                    .build();
        }
    }
}


