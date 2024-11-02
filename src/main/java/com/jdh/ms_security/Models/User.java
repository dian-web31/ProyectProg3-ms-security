package com.jdh.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {

    @Id
    private String _id;
    private String name;
    private String email;
    private String password;
    private String code2fa;

    public User() {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code2fa = code2fa;
    }

    public String getcode2fa() {
        return code2fa;
    }

    public void setcode2fa(String code2fa) {
        this.code2fa = code2fa;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}