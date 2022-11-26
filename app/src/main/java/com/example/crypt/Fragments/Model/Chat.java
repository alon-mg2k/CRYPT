package com.example.crypt.Fragments.Model;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private int typeMss;
    String password="g4sr7t";

    public Chat(String sender, String receiver, String message, int typeMss) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.typeMss = typeMss;
    }

    public Chat() {
    }

    public int getTypeMss() { return typeMss; }

    public void setTypeMss(int typeMss) { this.typeMss = typeMss; }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        try{
            message = desencriptar(message, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String desencriptar (String mensaje, String password) throws Exception{
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDeco = Base64.decode(mensaje, Base64.DEFAULT);
        byte[] datosDesencriptByte = cipher.doFinal(datosDeco);
        String datosDesencriptString = new String(datosDesencriptByte);
        return datosDesencriptString;
    }
    private SecretKey generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
}
