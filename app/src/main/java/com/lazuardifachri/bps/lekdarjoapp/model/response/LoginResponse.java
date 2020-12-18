package com.lazuardifachri.bps.lekdarjoapp.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class LoginResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("type")
    private String type;
    @SerializedName("roles")
    private String[] roles;
    @SerializedName("token")
    private String token;

    public LoginResponse(int id, String username, String type, String[] roles, String token) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.roles = roles;
        this.token = token;
    }

    public LoginResponse() {
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", token='" + token + '\'' +
                '}';
    }
}
