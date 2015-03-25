/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Date;
import javax.enterprise.inject.Model;

/**
 *
 * @author hth
 */
@Model
public class TrackPoint {
    private String latitude;
    private String longitude;
    private double elevation;
    private int speed;
    private int distance;
    private String timestamp;

    public TrackPoint() {
        
    }

    public TrackPoint(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
        
    public static TrackPoint fromDBObject(DBObject obj) {
        TrackPoint tp = new TrackPoint();
        
        if(obj != null) {
            tp.latitude = (String) obj.get("latitude");
            tp.longitude = (String) obj.get("longitude");
            tp.elevation = (double) obj.get("elevation");
            tp.distance = (int) obj.get("distance");
            tp.speed = (int) obj.get("speed");
            tp.timestamp = (String) obj.get("timestamp");
        }
        return tp;
    }
    
    public BasicDBObject toDBObject() {
        BasicDBObject obj = new BasicDBObject();

        obj.put("latitude", latitude);
        obj.put("longitude", longitude);
        obj.put("elevation", elevation);
        obj.put("distance", distance);
        obj.put("speed", speed);
        obj.put("timestamp", timestamp);

        return obj;
    } 
    
    @Override
    public String toString() {
        return latitude + ", " + longitude + ", " + elevation + ", " +
               distance + ", " + speed + ", " + timestamp;
    }
}
