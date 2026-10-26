package com.gicovi.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(
                            SecurityStarterAutoConfiguration.class
                    )
            ).
            withPropertyValues("gicovi.security.enabled=true");

    @Test
    void securityFilterChainCreatedWhenEnabled() {
        contextRunner.run(context -> {
            assertThat(context).
                    hasSingleBean(SecurityFilterChain.class);
        });
    }


    @Test
    void securityFilterChainIsNotCreatedWhenDisabled(){
        contextRunner.withPropertyValues("gicovi.security.enabled=false")
                .run(context ->
                        assertThat(context)
                                .doesNotHaveBean(SecurityFilterChain.class)
                );
    }
}
