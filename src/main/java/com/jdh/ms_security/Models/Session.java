package com.jdh.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Session {
    @Id
    private String _id;
    private String token;
    private String expiration;
    private String code2FA;

    @DBRef
    private User user;

    public Session(){

    }

    public Session(String code2FA, User user) {
        this.code2FA = code2FA;
        this.user = user;
    }

    public Session(String token, String expiration, String code2FA) {
        this.token = token;
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getcode2FA() {
        return code2FA;
    }

    public void setcode2FA(String code2FA) {
        this.code2FA = code2FA;
    }
}
