package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.User;

import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface InstanceService {

    Instance getInstance(int instanceId);

    Instance getInstanceByName(String instanceName);

    int findInstanceIdByName(String instanceName);

    void addInstance(int companyId, Instance instance);

    void editInstance(int instanceId, String longName);

    void editInstance(String instanceName, String instanceLongName, String instanceDescription);

    long getInstanceSize(int instanceId);

    void incrementInstanceSize(int instanceId);

    void decrementInstanceSize(int instanceId);

    Set<Instance> getInstancesForUser(User user);

    List<Instance> getAllInstances();

    void addUsersToInstance(int[] userIds, int instanceId);

    void removeUsersFromInstance(int[] userIds, int instanceId);

}
