package com.github.jdubois.responses.web.util;

/**
 * @author Julien Dubois
 */
public class ResponsesHtmlUtils {

    /**
     * Escape the HTML specific characters (like &lt;) but keeps the UTF-8 content (for characters like &eacute;).
     */
    public static String htmlEscape(String input) {
        if (input == null) {
            return null;
        }
        String result = input.replace("&", "&amp;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        return result;
    }
}
