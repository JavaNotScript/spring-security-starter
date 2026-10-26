package com.gicovi.security.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gicovi.security")
public class SecurityProperties {
    private boolean enable = true;
    private List<String> publicEndpoints = new ArrayList<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnabled(boolean enable) {
        this.enable = enable;
    }

    public List<String> getPublicEndpoints() {
        return publicEndpoints;
    }

    public void setPublicEndpoints(List<String> publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }
}
