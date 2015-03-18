/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

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
public class DataBean{
    
    private List tracks;
    private String mapCenter;
    private String username;
    private String buttonText;
    private Boolean disabled;

    /**
     * Creates a new instance of DataBean
     */
    public DataBean() {
        tracks = new ArrayList();
        tracks.add("65.0126144,25.4714526");
        tracks.add("61.4981508,23.7610254");
        tracks.add("66.4970212,25.724999");
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();        
        if(request.getParameter("track") == null)
            mapCenter = "65.0126144,25.4714526";
        else
            mapCenter = request.getParameter("track");
        username = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        disabled = true;
        buttonText = "Edit";
    }

    public List getTracks() {
        return tracks;
    }

    public void setTracks(List tracks) {
        this.tracks = tracks;
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
