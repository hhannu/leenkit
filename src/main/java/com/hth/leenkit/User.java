/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import javax.enterprise.inject.Model;

/**
 *
 * @author hth
 */
@Model
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String city;
    private String language;
    private Boolean metric;

    public User() {
        
    }

    public User(String username, String password, String email, String language, Boolean metric) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.language = language;
        this.metric = metric;        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getMetric() {
        return metric;
    }

    public void setMetric(Boolean metric) {
        this.metric = metric;
    }
    
    public static User fromDBObject(DBObject obj) {
        User u = new User();
        
        if(obj != null) {
            u.username = (String) obj.get("username");
            u.password = (String) obj.get("password");
            u.email = (String) obj.get("email");
            u.city = (String) obj.get("city");
            u.language = (String) obj.get("language");
            u.metric = (Boolean) obj.get("metric");
        }
        return u;
    }
    
    public BasicDBObject toDBObject() {
        BasicDBObject obj = new BasicDBObject();

        obj.put("username", username);
        obj.put("password", password);
        obj.put("email", email);
        obj.put("city", city);
        obj.put("language", language);
        obj.put("metric", metric);

        return obj;
    }    
}
