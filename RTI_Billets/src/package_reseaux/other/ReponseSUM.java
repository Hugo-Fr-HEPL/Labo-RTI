package package_reseaux.other;

import java.io.*;
import java.util.Vector;

import package_reseaux.Interface.Reponse;

public class ReponseSUM implements Reponse, Serializable {
    public static int CONNECTION = 201;
    public static int HANDSHAKE = 501;
    public static int ACHAT = 801;

    private int codeRetour;
    private String chargeUtile;
    private Vector vec;
    private byte[] repUtile;
    
    public ReponseSUM(int c, String chu, byte[] ru) {
        codeRetour = c; setChargeUtile(chu); setRepUtile(ru);
    }
    
    public ReponseSUM(int c, String chu, Vector v) {
        codeRetour = c; setChargeUtile(chu); setVec(v);
    }
    
    public ReponseSUM(int c, String chu) {
        codeRetour = c; setChargeUtile(chu);
    }
    
    public ReponseSUM(int c) {
        codeRetour = c; setChargeUtile("");
    }

    public int getCode() { return codeRetour; }
    
    public String getChargeUtile() { return chargeUtile; }
    public void setChargeUtile(String chargeU) { chargeUtile = chargeU; }
    
    public Vector getVec() { return vec; }
    public void setVec(Vector v) { vec = v; }
    
    public byte[] getRepUtile() { return repUtile; }
    public void setRepUtile(byte[] RU) { repUtile = RU; }
}
