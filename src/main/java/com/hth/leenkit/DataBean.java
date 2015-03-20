/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
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
public class DataBean {
    
    private List trackList;
    private String mapCenter;
    private String username;
    private String buttonText;
    private Boolean disabled;
    
    private Database db;
    private BasicDBObject track, tmpTrack;
    
    private MapModel polylineModel;
        
    private Polyline polyline;
    
        
    /**
     * Creates a new instance of DataBean
     */
    public DataBean() {
        trackList = new ArrayList();
        track = new BasicDBObject("duration", "00:00:00")
        .append("distance", "0.00")
        .append("avgSpeed", "0.00");
        
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
        for (BasicDBObject obj : points){
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
        trackList.clear();
        db = new Database("lenkit");
//        List<DBObject> tracklist = db.getTracks(username);
//        for (DBObject obj : tracklist) {
//            trackList.add((String)obj.get("name"));
//        }
        return db.getTracks(username); //tracks;
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
    
    public void editData() {
                
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
        
        db.saveTrack(track);
        
    }   
    
}
