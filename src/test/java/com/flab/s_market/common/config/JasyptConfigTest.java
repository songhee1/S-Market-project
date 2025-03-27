package com.flab.s_market.common.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

class JasyptConfigTest {
    public static void main(String[] args) {

        String password = "";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setAlgorithm("PBEWITHMD5ANDDES");
        jasypt.setPassword("");

        String encryptedText = jasypt.encrypt(password);
        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println("encryptedText = " + encryptedText);
        System.out.println("decryptedText = " + decryptedText);
    }
}