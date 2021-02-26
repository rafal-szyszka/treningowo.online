package com.prodactivv.app.core.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class HashGenerator {

    public String generateSha256Hash(List<String> parts) {
        StringBuilder builder = new StringBuilder();
        parts.forEach(builder::append);
        return DigestUtils.sha256Hex(builder.toString());
    }

    public String generateRandom(int limit) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(limit)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
