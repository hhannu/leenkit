/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;


/**
 *
 * @author hth
 */
@Singleton
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
       /* try {
            mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //mc = mongoClientProvider.getMongoClient();
        db = mc.getDB(databaseName);
        
        
        if(db.collectionExists(USERS))
            users = db.getCollection(USERS);
        else
            users = db.createCollection(USERS, new BasicDBObject("capped", false));
        
        if(db.collectionExists(TRACKS))
            tracks = db.getCollection(TRACKS);
        else
            users = db.createCollection(TRACKS, new BasicDBObject("capped", false));
               */
    }
    
    private void connect() {
    
        try {
            mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //mc = mongoClientProvider.getMongoClient();
        db = mc.getDB(databaseName);
    }
    
    private void close() {
        mc.close();
    }
    
    public Boolean hasUser(String name) {
        connect();
        Boolean val = db.getCollection(USERS).findOne(new BasicDBObject("username", name)) != null;
        close();
        return(val);
        
    }
    
    public DBObject getUser(String name) {
        connect();
        BasicDBObject tmp;
        tmp = new BasicDBObject("username", name);
        DBObject val = db.getCollection(USERS).findOne(tmp);
        close();
        return val;
    }
    
    public void addUser(BasicDBObject user) {
        connect();
        db.getCollection(USERS).insert(user);
        close();
    }
    
    public List<DBObject> getUsers() {
        connect();
        List<DBObject> val = users.find().toArray();
        close();
        return val;
    } 
    
    public void addTrack(BasicDBObject track) {
        connect();
        db.getCollection(TRACKS).insert(track);
        close();
    }
    
    public void saveTrack(BasicDBObject track) {
        connect();
        db.getCollection(TRACKS).save(track);
        close();
    }
    
    public List<DBObject> getTracks(String username) {
        connect();
        List<DBObject> list;
        
        if(db.getCollection(TRACKS).count() > 0) {
            DBCursor tmp = db.getCollection(TRACKS).find(new BasicDBObject("owner", username));
            list = tmp.toArray();
        }
        else
            list = new ArrayList();
        close();
        return list;
    }    
}
