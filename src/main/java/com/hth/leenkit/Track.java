/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.inject.Model;
import org.bson.types.ObjectId;

/**
 *
 * @author hth
 */
@Model
public class Track {
    
    private String owner;
    private String name;
    private String description;
    private String distance;
    private String duration;
    private String avgspeed;
    private String maxspeed;
    private Date timestamp;
    private List<TrackPoint> trackpoints;
    
    private ObjectId id;

    public Track() {
        id = null;
    }

    public Track(String owner, String name) {
        this.owner = owner;
        this.name = name;
        id = null;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAvgspeed() {
        return avgspeed;
    }

    public void setAvgspeed(String avgspeed) {
        this.avgspeed = avgspeed;
    }

    public String getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(String maxspeed) {
        this.maxspeed = maxspeed;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<TrackPoint> getTrackpoints() {
        return trackpoints;
    }

    public void setTrackpoints(List<TrackPoint> trackpoints) {
        this.trackpoints = trackpoints;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    
    
    public static Track fromDBObject(DBObject obj) {
        Track t = new Track();
                
        if(obj != null) {
            t.id = (ObjectId) obj.get("_id");
            t.name = (String) obj.get("name");
            t.owner = (String) obj.get("owner");
            t.description = (String) obj.get("description");
            t.distance = (String) obj.get("distance");
            t.duration = (String) obj.get("duration");
            t.avgspeed = (String) obj.get("avgspeed");
            t.maxspeed = (String) obj.get("maxspeed");
            t.timestamp = (Date) obj.get("timestamp");

            List<BasicDBObject> list = (List<BasicDBObject>) obj.get("trackpoints");  
        
            t.trackpoints = new ArrayList();
            if(list != null)
                for(BasicDBObject dbo : list) {
                    t.trackpoints.add(TrackPoint.fromDBObject(dbo));
                }
        }       
        return t;
    }
    
    public BasicDBObject toDBObject() {
        BasicDBObject obj = new BasicDBObject();
        if(id != null)
            obj.put("_id", id);
        obj.put("name", name);
        obj.put("owner", owner);
        obj.put("description", description);
        obj.put("distance", distance);
        obj.put("duration", duration);
        obj.put("avgspeed", avgspeed);
        obj.put("maxspeed", maxspeed);
        obj.put("timestamp", timestamp);

        List<BasicDBObject> points = new ArrayList();   
        for(TrackPoint tp : trackpoints) {
            points.add(tp.toDBObject());
        }
        obj.put("trackpoints", points);
        
        return obj;
    } 
    
    public Track copy() {
        Track t = new Track();
        
        t.id = this.id;
        t.name = this.name;
        t.owner = this.owner;
        t.description = this.description;
        t.distance = this.distance;
        t.duration = this.duration;
        t.avgspeed = this.avgspeed;
        t.maxspeed = this.maxspeed;
        t.timestamp = this.timestamp;
        t.trackpoints = this.trackpoints;
            
        return t;
    }
    
    @Override
    public String toString() {
        return id == null ? "null" : id.toString() + ", " + name + ", " + owner + ", " + description + ", " + distance + ", " + 
               duration + ", " + avgspeed + ", " + maxspeed + ", " +timestamp  + ", " + trackpoints;
    }
}
