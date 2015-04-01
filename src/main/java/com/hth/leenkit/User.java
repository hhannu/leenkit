/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Model;
import org.bson.types.ObjectId;
import org.primefaces.json.JSONObject;

/**
 *
 * @author hth
 */
@Model
public class User {
    private ObjectId id;
    private String username;
    private String password;
    private String email;
    private String location;
    private String language;
    private Boolean metric;

    public User() {
        id = null;        
    }

    public User(String username, String password, String email, String language, Boolean metric) {
        id = null;
        this.username = username;
        this.password = password;
        this.email = email;
        this.language = language;
        this.metric = metric;        
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
    
    public String getLocationCoords() {
        String loc = "";
        
        HttpURLConnection con = null ;
        InputStream is = null;
        
        JSONObject data;
        
        try {            
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + this.location);
            
	    con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");
	    con.setDoInput(true);
	    con.setDoOutput(true);
	    con.connect();
            
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = br.readLine()) != null)
                buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            data = new JSONObject(buffer.toString());
            loc = data.getJSONObject("coord").getString("lat") + "," +
                  data.getJSONObject("coord").getString("lon");
            
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);            
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        } 
        if(!loc.isEmpty())
            return loc;
        else
            return "65.0126144,25.4714526";
    }
    
    public static User fromDBObject(DBObject obj) {
        User u = new User();
        
        if(obj != null) {
            u.id = (ObjectId) obj.get("_id");
            u.username = (String) obj.get("username");
            u.password = (String) obj.get("password");
            u.email = (String) obj.get("email");
            u.location = (String) obj.get("location");
            u.language = (String) obj.get("language");
            u.metric = (Boolean) obj.get("metric");
        }
        return u;
    }
    
    public BasicDBObject toDBObject() {
        BasicDBObject obj = new BasicDBObject();
        
        if(id != null)
            obj.put("_id", id);
        obj.put("username", username);
        obj.put("password", password);
        obj.put("email", email);
        obj.put("location", location);
        obj.put("language", language);
        obj.put("metric", metric);

        return obj;
    }    
}
