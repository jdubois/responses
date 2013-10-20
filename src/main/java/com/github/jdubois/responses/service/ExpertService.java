package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;

import java.util.Collection;
import java.util.List;

/**
 * @author Julien Dubois
 */
public interface ExpertService {

    List<User> getExpertsForInstance(String instanceName);

    List<User> getExpertsForInstances(Collection<Instance> instances);

    List<Question> getQuestions(Collection<Instance> instances, int wfState, int assignedUserId, int index);

}
