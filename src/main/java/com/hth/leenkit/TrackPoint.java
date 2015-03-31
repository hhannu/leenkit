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
public class TrackPoint {
    private String latitude;
    private String longitude;
    private double elevation;
    private int speed;
    private long distance;
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

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
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
            tp.distance = (long) obj.get("distance");
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
    
    /**
     * Calculate distance between two TrackPoints
     * @param tp
     * @return distance in meters
     */
    public long distanceTo(TrackPoint tp) {
        // earth's radius
        double radius = 6371000;
        
        double lat1 = Double.parseDouble(tp.getLatitude());
        double lat2 = Double.parseDouble(this.getLatitude());
        double lon1 = Double.parseDouble(tp.getLongitude());
        double lon2 = Double.parseDouble(this.getLongitude());
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) 
                   * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return (long) (Math.round(radius * c));
    }
}
