package com.github.jdubois.responses.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Julien Dubois
 */
public class PasswordGenerator {

    private static final int passwordLength = 10;

    private static final char[] alphaNumberic = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    public static String generatePassword() {
        SecureRandom wheel = null;
        try {
            wheel = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            Log log = LogFactory.getLog(PasswordGenerator.class);
            log.error("No SHA1 algorithm, password generation cannot be processed!");
            return "changeme";
        }
        StringBuffer password = new StringBuffer();
        for (int i = 0; i < passwordLength; i++) {
            int random = wheel.nextInt(alphaNumberic.length);
            password.append(alphaNumberic[random]);
        }
        return password.toString();
    }
}
