package com.github.jdubois.responses.service;

import com.github.jdubois.responses.service.exception.HtmlValidationException;

/**
 * @author Julien Dubois
 */
public interface AntiSamyService {

    String cleanHtml(String taintedHTML) throws HtmlValidationException;
}
