package com.github.jdubois.responses.service;

import com.github.jdubois.responses.service.dto.InstanceStatistics;

import java.util.List;

/**
 * @author Julien Dubois
 */
public interface ReportingService {

    List<InstanceStatistics> getInstanceStatistics();
}
