/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hth.leenkit;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author hth
 */
@ManagedBean
@SessionScoped
public class LocaleBean {

    private Locale locale;
    private String localeCode;
    private static Map<String,Object> languages;
    private static List<String> units;
    private String unitString;
    
    /**
     * Creates a new instance of LocaleBean
     */
    public LocaleBean() {
        //locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        locale = Locale.ENGLISH;
        languages = new LinkedHashMap<String,Object>();
        languages.put("English", Locale.ENGLISH);
	languages.put("Finnish", new Locale("fi"));
        units = Arrays.asList("Metric", "Imperial");
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public Map<String, Object> getLanguages() {
        return languages;
    }

    public List<String> getUnits() {
        return units;
    }

    public String getUnitString() {
        return unitString;
    }

    public void setUnitString(String unit) {
        this.unitString = unit;
    }
	
    public void localeCodeChanged(ValueChangeEvent e){
 
        String newLocale = e.getNewValue().toString();

        for (Map.Entry<String, Object> entry : languages.entrySet()) {
            if(entry.getValue().toString().equals(newLocale)) {
                locale = (Locale)entry.getValue();
                FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue());
            }
        }
    }
    
}
