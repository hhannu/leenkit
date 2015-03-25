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
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hth
 */
@ManagedBean
@SessionScoped
public class DataBaseBean {

    private Track track;
    private User user;

    private DBCollection tracks;
    private DBCollection users;
    
    private List<Track> tracklist;
    private Boolean disabled;
    private String buttonText;
    private UploadedFile gpxFile;
    private Track tmpTrack;
    private String description;
    private String mapCenter;
    private Polyline polyline;
    private MapModel polylineModel;

    public DataBaseBean() {
        MongoClient mc = null;
        
        try {
            mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DataBaseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DB db = mc.getDB("leenkit");
        
        users = db.getCollection("users");
        if (users == null) {
            users = db.createCollection("users", null);
        }
        
        tracks = db.getCollection("tracks");
        if (tracks == null) {
            tracks = db.createCollection("tracks", null);
        }
        
        user = new User();
        track = null;
        tracklist = new ArrayList();     
        
        mapCenter = "65.0126144,25.4714526";
        
        polyline = new Polyline();
        polyline.setStrokeWeight(2);
        polyline.setStrokeColor("#FF0000");
        polyline.setStrokeOpacity(1.0);

        polylineModel = new DefaultMapModel();        
        polylineModel.addOverlay(polyline);
        
        disabled = true;
        buttonText = "Edit";
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        //System.out.println("setTrack() " + track);      
        this.track = track;
        
        List<LatLng> path = new ArrayList();
        List<TrackPoint> points = track.getTrackpoints();
        for(TrackPoint p : points){
            path.add(new LatLng(Double.parseDouble((String) p.getLatitude()),
                    Double.parseDouble((String) p.getLongitude())));
        }        
        polyline.setPaths(path);
        
        polylineModel = new DefaultMapModel();        
        polylineModel.addOverlay(polyline);
        if(!path.isEmpty()){
            polylineModel.addOverlay(new Marker(path.get(0), "Start"));
            polylineModel.addOverlay(new Marker(path.get(path.size() - 1), "Stop"));
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        //findTracks(user.getUsername());
    }
    
    public List<Track> getTracklist() {
        return tracklist;
    }

    public void setTracklist(List<Track> tracklist) {
        this.tracklist = tracklist;
    }

    public DBCollection getTracks() {
        return tracks;
    }

    public void setTracks(DBCollection tracks) {
        this.tracks = tracks;
    }

    public DBCollection getUsers() {
        return users;
    }

    public void setUsers(DBCollection users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public MapModel getPolylineModel() {
        return polylineModel;
    }

    public void setPolylineModel(MapModel polylineModel) {
        this.polylineModel = polylineModel;
    }      

    public String getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(String mapCenter) {
        this.mapCenter = mapCenter;
    }
    
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public UploadedFile getGpxFile() {
        return gpxFile;
    }

    public void setGpxFile(UploadedFile gpxFile) {
        this.gpxFile = gpxFile;
    }
    
    public Boolean hasUser(String username) {
        return users.findOne(new BasicDBObject("username", username)) != null;
    }
    
    public User findUser(String username) {        
        DBObject tmp = users.findOne(new BasicDBObject("username", username));
        if(tmp == null)
            return null;
        return User.fromDBObject(tmp);
    }
    
    public void addUser(User u) {
        users.insert(u.toDBObject());
    }
    
    public void addTrack(Track t) {
        tracks.insert(t.toDBObject());
    }
    
    public void saveTrack(Track t) {
        tracks.save(t.toDBObject());        
    }
    
    public String login() {     
        
        User tmp = findUser(user.getUsername());
        
        FacesContext context = FacesContext.getCurrentInstance(); 
        
        if(tmp != null){
            user = tmp;
            findTracks();
            System.out.println("Correct username & password");
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            return "content";        
        }
        else {        
            System.out.println("Wrong username and/or password");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Wrong username or password"));
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
    
    public String register() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        System.out.println("register: " + user.toString());  
        
        if(findUser(user.getUsername()) == null){     
            System.out.println("Adding username to database");       
            addUser(user);
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            
            return "content";
        }
        else {        
            System.out.println("Username already exists");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Username already exists"));
            return null;   
        }
    }
    
    private void findTracks() {
        
        tracklist.clear();
        
        DBCursor cur = tracks.find(new BasicDBObject("owner", user.getUsername()));
                
        //System.out.println("findTracks: Found " + cur.size() + " track(s)");
        for(DBObject obj : cur.toArray()) {
            //System.out.println(obj);
            System.out.println(Track.fromDBObject(obj).toString());
            tracklist.add(Track.fromDBObject(obj));
        }     
    }
   
    public void editData() {
         
        if(track == null)
            return;
        
        if(!this.disabled){
            track = tmpTrack;
            this.buttonText = "Edit";
            this.disabled = true;
        }
        else {
            tmpTrack = track.copy();
            this.buttonText = "Cancel";
            this.disabled = false;
        }
    }
    
    public void saveData() {
        this.buttonText = "Edit";
        this.disabled = true;
        
        if(track != null) {
            saveTrack(track);
        }
        
    }   
    
    public void uploadFile() {
        if(gpxFile != null) {
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            Document doc;
            
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(gpxFile.getInputstream());
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                Logger.getLogger(DataBaseBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesMessage message = new FacesMessage("Error", ex.getLocalizedMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            doc.getDocumentElement().normalize();
            
            NodeList trackpoints = doc.getElementsByTagName("trkpt");
            List<TrackPoint> points = new ArrayList();
            
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
                    if(nodeName.equals("time"))
                        trkpt.setTimestamp(nlist.item(j).getTextContent());
                }
                points.add(trkpt);
            }
            
            System.out.println(points);
            
            // Create new track object
            Track newTrack = new Track();
            newTrack.setOwner(user.getUsername());
            newTrack.setDescription(description);
            String name = doc.getElementsByTagName("name").item(0).getTextContent();
            if(name == null)
                name = points.get(0).getTimestamp();
            newTrack.setName(name);
            newTrack.setTrackpoints(points);
            
            saveTrack(newTrack);
            findTracks();
            
            FacesMessage message = new FacesMessage("Success", "Uploaded file: " + gpxFile.getFileName());
            FacesContext.getCurrentInstance().addMessage(null, message);
            
            System.out.println("Uploaded file: " + gpxFile.getFileName() + 
                               " (" + gpxFile.getContentType() + ")");
            System.out.println("Description: " + description);            
            System.out.println(newTrack + "\n");
            
            gpxFile = null;
            description = "";
        }
        
    }
    
    public void deleteTrack() {
        System.out.println("deleteTrack() " + track);
        tracks.remove(new BasicDBObject("_id", track.getId()));  
        track = null;      
        polylineModel = new DefaultMapModel(); 
        editData();
        findTracks();        
    }
    
    public void applySettings() {
    
    }
}
