/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author hth
 */
public class AuthorizationListener implements PhaseListener {
 
    @Override
    public void afterPhase(PhaseEvent event) {

        FacesContext context = event.getFacesContext();
 
        if (userExists(context)) {
            if(registerView(context)){
                context.responseComplete();
                context.getApplication().getNavigationHandler().handleNavigation(context, null, "content");
            
            }
            else    
                // allow processing of the requested view
                return;
        } else {  
            // send the user to the login view
            if (secureView(context)) {
                context.responseComplete();
                context.getApplication().getNavigationHandler().handleNavigation(context, null, "login");
            }
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {

    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
    
    private boolean userExists(FacesContext context) {
        ExternalContext extContext = context.getExternalContext();
        return(extContext.getSessionMap().containsKey("username"));
    }
    
    private boolean secureView(FacesContext context) {     
        String path = context.getExternalContext().getRequestPathInfo();
        return("/index.xhtml".equals(path));              
    }
    
    private boolean registerView(FacesContext context) {     
        String path = context.getExternalContext().getRequestPathInfo();
        return("/register.xhtml".equals(path));              
    }    
}
