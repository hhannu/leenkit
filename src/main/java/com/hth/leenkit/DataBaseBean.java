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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
import org.mindrot.jbcrypt.BCrypt;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
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
    private LineChartModel spdModel;
    private LineChartSeries series;

    ResourceBundle messages = ResourceBundle.getBundle("locale");
    
    /**
     * Constructor
     */
    public DataBaseBean() {
        MongoClient mc = null;
        
        String mongo_un = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
        String mongo_pw = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
        String mongo_host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        String mongo_port = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
        
        try {
            if(mongo_pw != null){    
                MongoCredential credential = MongoCredential.createMongoCRCredential(mongo_un, "leenkit", mongo_pw.toCharArray());
                mc = new MongoClient(new ServerAddress(mongo_host, Integer.parseInt(mongo_port)), Arrays.asList(credential));
            }
            else
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
        buttonText = messages.getString("edit");
        
        initLineModel();
        createLineModel();
    }
    
    /**
     *  Getters and setters 
     */

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        //System.out.println("setTrack() " + track);      
        this.track = track;
        
        List<LatLng> path = new ArrayList();
        List<TrackPoint> points = track.getTrackpoints();

        series = new LineChartSeries();
        series.setShowMarker(false);
        
        for(int i = 0; i < points.size(); i++){
            TrackPoint p = points.get(i);
            path.add(new LatLng(Double.parseDouble((String) p.getLatitude()),
                    Double.parseDouble((String) p.getLongitude())));            
            series.set(p.getDistance(), p.getSpeed() * 3.6);
                          
        }        
        polyline.setPaths(path);
        
        polylineModel = new DefaultMapModel();        
        polylineModel.addOverlay(polyline);
        if(!path.isEmpty()){
            mapCenter = path.get(0).getLat() + "," + path.get(0).getLng();
            polylineModel.addOverlay(new Marker(path.get(0), "Start"));
            polylineModel.addOverlay(new Marker(path.get(path.size() - 1), "Stop"));
        }
        
        createLineModel();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LineChartModel getSpdModel() {
        return spdModel;
    }

    public void setSpdModel(LineChartModel spdModel) {
        this.spdModel = spdModel;
    }    
    
    /**
     * Add user to database.
     * @param u 
     */
    public void addUser(User u) {
        users.insert(u.toDBObject());
    }
    
    /**
     * Add track to database.
     * @param t
     */
    public void addTrack(Track t) {
        tracks.insert(t.toDBObject());
    }
    
    /**
     * Save track to database.
     * @param t
     */
    public void saveTrack(Track t) {
        tracks.save(t.toDBObject());        
    }
    
    /**
     * Check if user is in the database
     * @param username
     * @return 
     */
    public Boolean hasUser(String username) {
        return users.findOne(new BasicDBObject("username", username)) != null;
    }
    
    /**
     * Get user from the database
     * @param username
     * @return 
     */
    public User findUser(String username) {        
        DBObject tmp = users.findOne(new BasicDBObject("username", username));
        if(tmp == null)
            return null;
        return User.fromDBObject(tmp);
    }
    
    /**
     * Log in.
     * @return 
     */
    public String login() {     
        
        User tmp = findUser(user.getUsername());
        
        FacesContext context = FacesContext.getCurrentInstance(); 
        
        if(tmp != null ){ // && BCrypt.checkpw(user.getPassword(), tmp.getPassword())){
            user = tmp;
            findTracks();
            //System.out.println("Correct username & password");
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            return "content";        
        }
        else {        
            //System.out.println("Wrong username and/or password");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Wrong username or password"));
            return null;
        }
    }
           
    /**
     * Log out.
     * @return 
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        
        HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        return "login";
    }
    
    /**
     * Register new user.
     * @return 
     */
    public String register() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        //System.out.println("register: " + user.toString());  
        
        if(findUser(user.getUsername()) == null){     
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(13)));
            //System.out.println("Adding username to database");       
            addUser(user);
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            
            return "content";
        }
        else {        
            //System.out.println("Username already exists");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",  "Username already exists"));
            return null;   
        }
    }
    
    /**
     * Find tracks for user currently logged in.
     */
    private void findTracks() {
        
        tracklist.clear();
        
        if(!user.getUsername().isEmpty()) {
            DBCursor cur = tracks.find(new BasicDBObject("owner", user.getUsername()));

            //System.out.println("findTracks: Found " + cur.size() + " track(s)");
            for(DBObject obj : cur.toArray()) {
                //System.out.println(obj);
                //System.out.println(Track.fromDBObject(obj).toString());
                tracklist.add(Track.fromDBObject(obj));
            }     
        }
    }
   
    /**
     * Enable data editing for currently selected track.
     */
    public void editData() {
         
        if(track == null)
            return;
        
        if(this.disabled){
            tmpTrack = track.copy();
            this.buttonText = messages.getString("cancel");
            this.disabled = false;
        }
        else {
            track = tmpTrack;
            this.buttonText = messages.getString("edit");
            this.disabled = true;
        }
    }
    
    /**
     * Save track data.
     */
    public void saveData() {
        this.buttonText = messages.getString("edit");
        this.disabled = true;
        
        if(track != null) {
            saveTrack(track);
        }
        
    }   
    
    /**
     * Upload new file to server.
     * Add new track to database if file is correct GPX file.
     */    
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
            Instant time_prev, time_now = null;
            long d = 0;
            long distance = 0;
            int maxspeed = 0;
        
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
                time_prev = time_now;
                time_now = Instant.parse(trkpt.getTimestamp());
                if(i == 0) {
                    trkpt.setSpeed(0);
                    trkpt.setDistance(0);
                }
                else {
                    //d = time_now.getEpochSecond() - time_prev.getEpochSecond();
                    d = Instant.parse(trkpt.getTimestamp()).getEpochSecond() -
                        Instant.parse(points.get(i - 1).getTimestamp()).getEpochSecond();
                    long dist = trkpt.distanceTo(points.get(i - 1));
                    distance += dist;
                    int speed = 0;
                    if(d != 0)
                        speed = (int) (dist / d);
                    if(speed > maxspeed)
                        maxspeed = speed;
                    trkpt.setSpeed(speed);
                    trkpt.setDistance(distance);
                }
                points.add(trkpt);
            }
            
            //System.out.println(points);
            
            // Create new track object
            Track newTrack = new Track();
            newTrack.setOwner(user.getUsername());
            newTrack.setDescription(description);
            // track name
            String name = doc.getElementsByTagName("name").item(0).getTextContent();
            if(name == null)
                name = points.get(0).getTimestamp();
            newTrack.setName(name);
            // distance
            newTrack.setDistance(String.valueOf(distance / 1000));
            // maxspeed
            newTrack.setMaxspeed(String.valueOf(maxspeed));
            // duration
            long seconds = Instant.parse(points.get(points.size() - 1).getTimestamp()).getEpochSecond() -
                        Instant.parse(points.get(0).getTimestamp()).getEpochSecond();
            newTrack.setDuration(String.format("%02d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60));
            // average speed
            newTrack.setAvgspeed(String.format("%.2f%n", ((double)distance / (double)seconds) * 3.6));
            newTrack.setTrackpoints(points);
            
            saveTrack(newTrack);
            findTracks();
            
            FacesMessage message = new FacesMessage("Success", "Uploaded file: " + gpxFile.getFileName());
            FacesContext.getCurrentInstance().addMessage(null, message);
            
            System.out.println("Uploaded file: " + gpxFile.getFileName() + 
                               " (" + gpxFile.getContentType() + ")");
            System.out.println("Description: " + description);            
            //System.out.println(newTrack + "\n");
            
            gpxFile = null;
            description = "";
        }
        
    }
    
    /**
     * Delete selected track from database.
     */
    public void deleteTrack() {
        System.out.println("deleteTrack() " + track);
        tracks.remove(new BasicDBObject("_id", track.getId()));       
        polylineModel = new DefaultMapModel(); 
        editData();
        track = null; 
        mapCenter = "65.0126144,25.4714526";
        findTracks(); 
        initLineModel();
        createLineModel();
    }
    
    public void applySettings() {
    
    }
    
    private void initLineModel() {
        series = new LineChartSeries();          
        series.set(0, 0);
    } 
    
    private void createLineModel() {
        spdModel = new LineChartModel();
 
        spdModel.addSeries(series);
        spdModel.setAnimate(true);        
        Axis yAxis = spdModel.getAxis(AxisType.Y);
        Axis xAxis = spdModel.getAxis(AxisType.X);
        yAxis.setMin(0);
        xAxis.setMin(0);
        yAxis.setLabel("Speed");
        xAxis.setLabel("Distance");
        if(track != null) {
            spdModel.setTitle(track.getName());
            yAxis.setMax(Double.parseDouble(track.getMaxspeed()) * 3.6);
            xAxis.setMax(track.getDistance());
        }
        else {
            spdModel.setTitle("");
            yAxis.setMax(10);            
        }
        
    }
}
