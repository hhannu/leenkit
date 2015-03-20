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
    private DBObject track;

    /**
     * Creates a new instance of DataBean
     */
    public DataBean() {
        trackList = new ArrayList();
        track = new BasicDBObject("duration", "00:00:00")
        .append("distance", "0.00")
        .append("avgSpeed", "0.00");
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        if(request.getParameter("track") == null)
            mapCenter = "65.0126144,25.4714526";
        else
            mapCenter = request.getParameter("track");
        username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        disabled = true;
        buttonText = "Edit";
    }

    public DBObject getTrack() {
        return track;
    }

    public void setTrack(DBObject track) {
        this.track = track;
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
    
    public void setTrack() {
        
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
            this.buttonText = "Edit";
            this.disabled = true;
        }
        else {
            this.buttonText = "Cancel";
            this.disabled = false;
        }
    }
    
    public void saveData() {
        this.buttonText = "Edit";
        this.disabled = true;
        
        //TODO: save data
    }   
    
}
