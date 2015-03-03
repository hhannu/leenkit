/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author hth
 */

@ManagedBean
@SessionScoped
public class UserManager {
    
    private String username, password1, password2, email;

    public UserManager() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String login(){
        
        boolean result = checkCredentials(username, password1);
        FacesContext context = FacesContext.getCurrentInstance(); 
        
            if(result){
                    context.getExternalContext().getSessionMap().put("username", username);
                    return "content";        
            }
            else {        
                context.addMessage(null, new FacesMessage("Error",  "Wrong username or password"));
                return null;
            }
    }
    
    public String register(){
        
        return "login";
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        
        HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        return "login";
    }
    
    private boolean checkCredentials(String username, String password) {
        return (username.equals("user") && password.equals("pw"));
    }
}
