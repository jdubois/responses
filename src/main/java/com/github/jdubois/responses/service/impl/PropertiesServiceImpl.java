package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.service.PropertiesService;

import java.io.Serializable;

/**
 * @author Julien Dubois
 */
public class PropertiesServiceImpl implements PropertiesService, Serializable {

    private String staticContent;

    private String siteUrl;

    private String googleAnalytics;

    public String getStaticContent() {
        return staticContent;
    }

    public void setStaticContent(String staticContent) {
        this.staticContent = staticContent;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getGoogleAnalytics() {
        return googleAnalytics;
    }

    public void setGoogleAnalytics(String googleAnalytics) {
        this.googleAnalytics = googleAnalytics;
    }
}
