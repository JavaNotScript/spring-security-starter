package com.gicovi.security.config;

import com.gicovi.security.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "org.springframework.security.config.annotation.web.builders.HttpSecurity") //prevents jvm from loading missing classes before condition is evaluated
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(
        prefix = "gicovi.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SecurityStarterAutoConfiguration {

    //protects the autoconfiguration from being activated when the required classes are not available
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.security.web.SecurityFilterChain")
    static class SecurityFilterChainConfiguration {

        @Bean
        @ConditionalOnMissingBean(SecurityFilterChain.class)
        public SecurityFilterChain securityFilterChain(HttpSecurity security, SecurityProperties securityProperties){

            return security
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(request -> {
                                if (!securityProperties.getPublicEndpoints().isEmpty()){
                                    request.requestMatchers(
                                                    securityProperties.getPublicEndpoints().toArray(String[]::new))
                                            .permitAll();
                                }
                                request.anyRequest().authenticated();
                            }
                    )
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }

    }
}
