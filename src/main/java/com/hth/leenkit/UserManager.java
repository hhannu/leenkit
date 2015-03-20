/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author hth
 */

@ManagedBean
@RequestScoped
public class UserManager {
    
    private String username, password1, password2, email;

    private MongoClient mc;
    //private DB db;
    private DBCollection users;
    private DBObject user;
    
    private Database db;
    
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
    
    public String login() {
        
        db = new Database("lenkit");
        
        boolean result = checkCredentials(username, password1);
        FacesContext context = FacesContext.getCurrentInstance(); 
        
        if(result){
            System.out.println("Correct username & password");
            context.getExternalContext().getSessionMap().put("username", username);
            return "content";        
        }
        else {        
            System.out.println("Wrong username and/or password");
            context.addMessage(null, new FacesMessage("Error",  "Wrong username or password"));
            return null;
        }
    }
    
    public String register() {
        
        db = new Database("lenkit");
        FacesContext context = FacesContext.getCurrentInstance();
        
        if(!db.hasUser(username)){     
            System.out.println("Adding username to database");       
            BasicDBObject tmp = new BasicDBObject("username", username).append("password",
                    BCrypt.hashpw(password1, BCrypt.gensalt(8))).append("email", email);
            db.addUser(tmp);
            context.getExternalContext().getSessionMap().put("username", username);
            return "content";
        }
        else {        
            System.out.println("Username already exists");
            context.addMessage(null, new FacesMessage("Error",  "Username already exists"));
            return null;   
        }
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
        /*
        try {
            mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Connected to DB");
        db = mc.getDB("lenkit");
        users = db.getCollection("users");
        
        BasicDBObject tmp = new BasicDBObject("username", username);
        user = users.findOne(tmp);
        mc.close();
        */
        user = db.getUser(username);
        
        System.out.println("Found user: " + user.toString());
        
        if(user == null)
            return false;
        
        System.out.println((String) user.get("password"));
        return(BCrypt.checkpw(password, (String) user.get("password")));
        
        //return (username.equals("user") && password.equals("pw"));
    }
}
