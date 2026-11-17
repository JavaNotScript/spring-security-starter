package com.gicovi.security.config;

import com.gicovi.security.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@AutoConfiguration(beforeName = "org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(
        prefix = "gicovi.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SecurityStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity security, SecurityProperties securityProperties){

        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    if (!securityProperties.getPublicEndpointsList().isEmpty()){
                        request.requestMatchers(
                                securityProperties.getPublicEndpointsList().toArray(String[]::new))
                                .permitAll();
                    }
                    request.anyRequest().authenticated();
                        }
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}