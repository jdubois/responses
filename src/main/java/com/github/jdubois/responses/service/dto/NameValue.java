package com.github.jdubois.responses.service.dto;

import java.io.Serializable;

/**
 * Generic name-value object.
 *
 * @author Julien Dubois
 */
public class NameValue implements Serializable {

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
