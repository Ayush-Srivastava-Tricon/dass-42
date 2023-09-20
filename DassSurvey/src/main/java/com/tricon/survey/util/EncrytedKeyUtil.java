package com.tricon.survey.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedKeyUtil {

	// Encrypt Password with BCryptPasswordEncoder
    public static String encryptKey(String key) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(key);
    }
}
