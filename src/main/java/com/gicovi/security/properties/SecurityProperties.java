package com.gicovi.security.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gicovi.security")
@EnableConfigurationProperties
public class SecurityProperties {
    private boolean enabled = true;
    private List<String> publicEndpointsList = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPublicEndpointsList() {
        return publicEndpointsList;
    }

    public void setPublicEndpointsList(List<String> publicEndpointsList) {
        this.publicEndpointsList = publicEndpointsList;
    }
}
