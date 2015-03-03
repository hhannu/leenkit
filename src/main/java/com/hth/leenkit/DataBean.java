/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author hth
 */
@ManagedBean
@RequestScoped
public class DataBean{
    
    private List tracks;
    private String mapCenter;

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
}
