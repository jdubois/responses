package com.github.jdubois.responses.web.instance.dto;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Julien Dubois
 */
public class UserDto {

    int id;

    String email;

    String firstName;

    String lastName;

    Collection<String> roles;

    boolean enabled;

    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        roles.add(role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
