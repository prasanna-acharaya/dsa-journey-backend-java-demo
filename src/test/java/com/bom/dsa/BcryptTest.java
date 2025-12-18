package com.bom.dsa;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    @Test
    void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String encodedPassword = encoder.encode(password);
        System.out.println("BCRYPT_HASH_FOR_PASSWORD123: " + encodedPassword);
    }
}
