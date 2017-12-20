/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.util;

import java.nio.charset.Charset;
import java.security.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class SecurityUtil {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Charset UTF8= Charset.forName("UTF-8");
    
    public static String generateSalt() {
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        return new String(Base64.getEncoder().encode(salt),UTF8);
    }
    
    public static String generateHash(String password, String salt) {
        byte[] passwordB = password.getBytes(UTF8);
        byte[] saltB = Base64.getDecoder().decode(salt.getBytes(UTF8));
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] saltyPassword = Arrays.copyOf(passwordB, passwordB.length+saltB.length);
            System.arraycopy(saltB, 0, saltyPassword,passwordB.length , saltB.length);
            byte[] hashB = md.digest(saltyPassword);
            return new String(Base64.getEncoder().encode(hashB),UTF8);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecurityUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
