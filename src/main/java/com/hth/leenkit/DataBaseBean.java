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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

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
    
    private String oldPassword;
    private String newPassword;

    ResourceBundle messages;    
    
    private Locale locale;

    private static final Map<String, String> availableLanguages;

    static {
        availableLanguages = new LinkedHashMap<>();
        availableLanguages.put("English", "en");
        availableLanguages.put("Finnish", "fi");
    }
    
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
                MongoCredential credential = MongoCredential.createMongoCRCredential(mongo_un, "lenkit", mongo_pw.toCharArray());
                mc = new MongoClient(new ServerAddress(mongo_host, Integer.parseInt(mongo_port)), Arrays.asList(credential));
            }
            else
                mc = new MongoClient("localhost", 27017);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DataBaseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DB db = mc.getDB("lenkit");
        
        users = db.getCollection("users");
        if (users == null) {
            users = db.createCollection("users", null);
        }
        
        tracks = db.getCollection("tracks");
        if (tracks == null) {
            tracks = db.createCollection("tracks", null);
        }
        
        //locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        locale = Locale.ENGLISH;
        messages = ResourceBundle.getBundle("locale", locale);
        
        user = new User();
        track = null;
        tracklist = new ArrayList();     
        
        mapCenter = user.getLocationCoords();
        
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public Map<String, String> getAvailableLanguages() {
        return availableLanguages;
    }

    public Locale getLocale() {
        return locale;
    }
        
    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
        
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
    
    /**
     * Add user to database.
     * @param u 
     */
    public void addUser(User u) {
        users.insert(u.toDBObject());
    }
    
    /**
     * Save user to database.
     * @param u 
     */
    public void saveUser(User u) {
        users.save(u.toDBObject());
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
        
        if(tmp != null && BCrypt.checkpw(user.getPassword(), tmp.getPassword())){
            user = tmp;
            if(!user.getLanguage().equals(""))
                locale = new Locale(user.getLanguage());
            FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
            messages = ResourceBundle.getBundle("locale", locale);
            buttonText = messages.getString("edit");
            findTracks();
            mapCenter = user.getLocationCoords();
            //System.out.println("Correct username & password");
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            return "content";        
        }
        else {        
            //System.out.println("Wrong username and/or password");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messages.getString("error"),  messages.getString("invalid_unpw")));
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
            user.setLocation("oulu,fi");
            user.setLanguage(getLanguage());
            mapCenter = "65.0126144,25.4714526";
            addUser(user);
            context.getExternalContext().getSessionMap().put("username", user.getUsername());
            
            return "content";
        }
        else {        
            //System.out.println("Username already exists");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messages.getString("error"),  messages.getString("user_exists")));
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
        
        if(disabled){
            tmpTrack = track.copy();
            buttonText = messages.getString("cancel");
            disabled = false;
        }
        else {
            track = tmpTrack;
            buttonText = messages.getString("edit");
            disabled = true;
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
            
            Track newTrack = null;
            
            try {
                newTrack = Track.createFromInputStream(gpxFile.getInputstream());
            } catch (IOException ex) {
                Logger.getLogger(DataBaseBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(newTrack != null){
                newTrack.setOwner(user.getUsername());
                newTrack.setDescription(description);
                saveTrack(newTrack);
                findTracks();

                FacesMessage message = new FacesMessage();
                message.setSummary(messages.getString("upload_file"));
                message.setDetail(gpxFile.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);

                System.out.println("Uploaded file: " + gpxFile.getFileName() + 
                                   " (" + gpxFile.getContentType() + ")");       
                //System.out.println(newTrack + "\n");
            }
            else {                
                FacesMessage message = new FacesMessage();
                message.setSummary(messages.getString("upload_file_fail"));
                message.setDetail(gpxFile.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            
            gpxFile = null;
            description = "";
        }        
    }
    
    /**
     * Delete selected track from database.
     */
    public void deleteTrack() {
        //System.out.println("deleteTrack() " + track);
        tracks.remove(new BasicDBObject("_id", track.getId()));       
        polylineModel = new DefaultMapModel(); 
        editData();
        track = null; 
        mapCenter = user.getLocationCoords();
        findTracks(); 
        initLineModel();
        createLineModel();
    }
    
    public void applySettings() {
        
        user.setLanguage(getLanguage());
        messages = ResourceBundle.getBundle("locale", locale);
        if(disabled)
            buttonText = messages.getString("edit");
        else
            buttonText = messages.getString("cancel");
        saveUser(user);
        if(track == null)
            mapCenter = user.getLocationCoords();
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
        yAxis.setLabel("km/h");
        xAxis.setLabel("m");
        if(track != null) { 
            spdModel.setZoom(true);   
            spdModel.setTitle(track.getName());
            yAxis.setMax(Double.parseDouble(track.getMaxspeed()));
            xAxis.setMax((long)(Double.parseDouble(track.getDistance()) * 1000));
        }
        else {
            spdModel.setZoom(false);
            spdModel.setTitle(" ");
            yAxis.setMax(10);            
        }        
    }
    
    public void changePassword(Boolean change) {
        
        FacesMessage message = new FacesMessage();
        
        if(change) {
            if(BCrypt.checkpw(oldPassword, user.getPassword())) {
                user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(13)));
                saveUser(user);
                message.setSummary(messages.getString("pw_changed"));
                message.setDetail("");
            }
            else {            
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(messages.getString("pw_notchanged"));
                message.setDetail(messages.getString("wrong_pw"));
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        
        this.oldPassword = "";
        this.newPassword = "";
    }
}
