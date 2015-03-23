/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
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
public class DataBean {
    
    private List trackList;
    private String mapCenter;
    private String username;
    private String buttonText;
    private String description;    
    private Boolean disabled;
    
    private Database db;
    private BasicDBObject track, tmpTrack;
    
    private MapModel polylineModel;
        
    private Polyline polyline;
    
    private UploadedFile gpxFile;
    
        
    /**
     * Creates a new instance of DataBean
     */
    public DataBean() {        
        db = new Database("leenkit");
        
        track = null;
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        //if(request.getParameter("track") == null)
            mapCenter = "65.0126144,25.4714526";
        //else
        //    mapCenter = request.getParameter("track");
        
        polyline = new Polyline();
        polyline.setStrokeWeight(2);
        polyline.setStrokeColor("#FF0000");
        polyline.setStrokeOpacity(1.0);

        polylineModel = new DefaultMapModel();        
        polylineModel.addOverlay(polyline);
        
        username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        disabled = true;
        buttonText = "Edit";
        
        updateTrackList();
        
    }

    public MapModel getPolylineModel() {
        return polylineModel;
    }

    public DBObject getTrack() {
        return track;
    }

    public void setTrack(BasicDBObject track) {
        this.track = track;
        List<LatLng> path = new ArrayList();
        List<BasicDBObject> points = (List<BasicDBObject>) track.get("trackPoints");
        for(BasicDBObject obj : points){
            path.add(new LatLng(Double.parseDouble((String) obj.get("lat")),
                    Double.parseDouble((String) obj.get("lon"))));
        }
        polyline.setPaths(path);
        
        polylineModel = new DefaultMapModel();        
        polylineModel.addOverlay(polyline);
        polylineModel.addOverlay(new Marker(path.get(0), "Start"));
        polylineModel.addOverlay(new Marker(path.get(path.size() - 1), "Stop"));
    }

    public List getTrackList() {
        //trackList.clear();
//        List<DBObject> tracklist = db.getTracks(username);
//        for (DBObject obj : tracklist) {
//            trackList.add((String)obj.get("name"));
//        }
        //return db.getTracks(username); //tracks;
        return trackList;
    }

    public void setTrackList(List tracks) {
        this.trackList = tracks;
    }

    public String getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(String mapCenter) {
        this.mapCenter = mapCenter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    public void editData() {
         
        if(track == null)
            return;
        
        if(!this.disabled){
            track = tmpTrack;
            this.buttonText = "Edit";
            this.disabled = true;
        }
        else {
            tmpTrack = (BasicDBObject) track.copy();
            this.buttonText = "Cancel";
            this.disabled = false;
        }
    }
    
    public void saveData() {
        this.buttonText = "Edit";
        this.disabled = true;
        
        if(track != null) {
            db.saveTrack(track);
            updateTrackList();
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
                Logger.getLogger(DataBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesMessage message = new FacesMessage("Error", ex.getLocalizedMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            doc.getDocumentElement().normalize();
            
            NodeList trackpoints = doc.getElementsByTagName("trkpt");
            List<BasicDBObject> points = new ArrayList();
            
            for(int i = 0; i < trackpoints.getLength(); i++) {
                Node point = trackpoints.item(i);
                BasicDBObject trkpt =  new BasicDBObject();
                // get latitude & longitude
                trkpt.append("lat", point.getAttributes().getNamedItem("lat").getNodeValue());
                trkpt.append("lon", point.getAttributes().getNamedItem("lon").getNodeValue());
                
                // get other parameters
                NodeList nlist = point.getChildNodes();
                for(int j = 0; j < nlist.getLength(); j++) {
                    String nodeName = nlist.item(j).getNodeName();
                    // add only elevation and time
                    if(nodeName.equals("ele") || nodeName.equals("time"))
                        trkpt.append(nlist.item(j).getNodeName(), nlist.item(j).getTextContent());                    
                }
                points.add(trkpt);
            }
            
            // Create new track object
            BasicDBObject newTrack = new BasicDBObject();
            newTrack.append("owner", username);
            //newTrack.append("description", description);
            String name = doc.getElementsByTagName("name").item(0).getNodeValue();
            if(name == null)
                name = points.get(0).getString("time");
            newTrack.append("name", doc.getElementsByTagName("name").item(0).getTextContent());
            
            newTrack.append("trackPoints", points);
            
            db.saveTrack(newTrack);
            updateTrackList();
            
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
    
    public void applySettings() {
    
    }
      
    private void updateTrackList() {        
        trackList = db.getTracks(username);
    }
}
