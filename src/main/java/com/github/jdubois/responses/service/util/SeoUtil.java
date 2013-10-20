package com.github.jdubois.responses.service.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 * Transform a String into a Search Engine Optimized (SEO) String.
 *
 * @author Julien Dubois
 */
public class SeoUtil {

    public static String seo(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        String result = input.toLowerCase();
        result = result.replaceAll(" ", "-");
        result = Normalizer.normalize(result, Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        result = result.replaceAll("\\?", "-");
        result = result.replaceAll("&", "-");
        result = result.replaceAll("%", "-");
        result = result.replaceAll("/", "-");
        result += ".html";
        return result;
    }

    public static String seoFromEscapedHtml(String input) {
        String result = input.replace("&amp;", "-");
        result = result.replace("&lt;", "-");
        result = result.replace("&gt;", "-");
        result = result.replace("&quot;", "-");
        return seo(result);
    }

}
