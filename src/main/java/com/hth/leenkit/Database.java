/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author hth
 */
//@Singleton
public class Database {        
    
    private MongoClient mc = null;
    
    private final String USERS = "users";
    private final String TRACKS = "tracks";
    private final String databaseName;
    
    private DB db;
    private DBCollection users, tracks;
    
    public Database() {
        databaseName = "";
    }

    public Database(String databaseName) {
        this.databaseName = databaseName;
        try {
            mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        db = mc.getDB(databaseName);
        users = db.getCollection(USERS);
        tracks = db.getCollection(TRACKS);
    }
    
    public Boolean hasUser(String name) {
        return(users.findOne(new BasicDBObject("username", name)) != null);
    }
    
    public DBObject getUser(String name) {
        BasicDBObject tmp;
        tmp = new BasicDBObject("username", name);
        return users.findOne(tmp);
    }
    
    public void addUser(BasicDBObject user) {
        users.insert(user);
    }
    
    public void addTrack(BasicDBObject track) {
        tracks.insert(track);
    }
    
    public List<DBObject> getTracks(String username) {
        return tracks.find(new BasicDBObject("owner", username)).toArray();
    }    
}
