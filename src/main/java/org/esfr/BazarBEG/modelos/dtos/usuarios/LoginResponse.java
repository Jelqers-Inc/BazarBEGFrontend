package org.esfr.BazarBEG.modelos.dtos.usuarios;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty("token")
    private String accessToken;
    @JsonProperty("user")
    private UserPayload userPayload;



    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserPayload getUserPayload() {
        return userPayload;
    }

    public void setUserPayload(UserPayload userPayload) {
        this.userPayload = userPayload;
    }
}
