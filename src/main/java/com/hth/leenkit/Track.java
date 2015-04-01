/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Model;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bson.types.ObjectId;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    
    public static Track createFromInputStream(InputStream is) {
        Track newTrack = null;
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc;

        // parse xml document from inputstream
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        doc.getDocumentElement().normalize();

        // get all trackpoints from xml document
        NodeList trackpoints = doc.getElementsByTagName("trkpt");
        List<TrackPoint> points = new ArrayList();

        long d = 0;
        long distance = 0;
        double speed = 0;
        double maxspeed = 0;

        // create list of TrackPoint objects
        for(int i = 0; i < trackpoints.getLength(); i++) {
            Node point = trackpoints.item(i);
            TrackPoint trkpt =  new TrackPoint();
            // get latitude & longitude
            trkpt.setLatitude(point.getAttributes().getNamedItem("lat").getNodeValue());
            trkpt.setLongitude(point.getAttributes().getNamedItem("lon").getNodeValue());

            // get other parameters
            NodeList nlist = point.getChildNodes();
            for(int j = 0; j < nlist.getLength(); j++) {
                String nodeName = nlist.item(j).getNodeName();
                // add only elevation and time
                if(nodeName.equals("ele"))
                    trkpt.setElevation(Float.parseFloat(nlist.item(j).getTextContent()));
                if(nodeName.equals("time")){
                    trkpt.setTimestamp(nlist.item(j).getTextContent());                        
                }
            }

            if(i == 0) {
                trkpt.setSpeed(0);
                trkpt.setDistance(0);
            }
            else {                
                d = Instant.parse(trkpt.getTimestamp()).getEpochSecond() -
                    Instant.parse(points.get(points.size() - 1).getTimestamp()).getEpochSecond();
                long dist = trkpt.distanceTo(points.get(points.size() - 1));
                distance += dist;
                if(d != 0 && dist != 0) {
                    speed = dist / d;
                    if(speed > maxspeed)
                        maxspeed = speed;
                }
                trkpt.setSpeed(speed);
                trkpt.setDistance(distance);
            }
            points.add(trkpt);
        }

        //System.out.println(points);

        // Create new Track object
        newTrack = new Track();
        // name
        String name = doc.getElementsByTagName("name").item(0).getTextContent();
        if(name == null)
            name = points.get(0).getTimestamp();
        newTrack.setName(name);
        // distance
        newTrack.setDistance(String.format("%.3f%n", (double)distance / 1000));
        // maxspeed
        newTrack.setMaxspeed(String.format("%.1f%n", maxspeed * 3.6));
        // duration
        long seconds = Instant.parse(points.get(points.size() - 1).getTimestamp()).getEpochSecond() -
                    Instant.parse(points.get(0).getTimestamp()).getEpochSecond();
        newTrack.setDuration(String.format("%02d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60));
        // average speed
        newTrack.setAvgspeed(String.format("%.1f%n", ((double)distance / (double)seconds) * 3.6));
        newTrack.setTrackpoints(points);
            System.out.println(points.get(points.size() - 1).getDistance());
        return newTrack;
    }
}
