package com.twixlmedia.entitlements.regions.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EntitlementsResponse {

    @JsonProperty("entitled_products")
    private List<String> entitledProducts;
    private String token;
    private String mode;

    public List<String> getEntitledProducts() {
        return entitledProducts;
    }

    public void setEntitledProducts(List<String> entitledProducts) {
        this.entitledProducts = entitledProducts;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
