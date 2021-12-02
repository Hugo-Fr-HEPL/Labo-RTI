package package_reseaux.other;

import java.io.*;
import java.util.Vector;

import package_reseaux.Interface.Reponse;

public class ReponseSUM implements Reponse, Serializable {
    public static int CONNECTION_OK = 201;
    public static int BAGAGE = 501;
    public static int KEY_GENERATED = 202;
    public static int WRONG_PASSWORD = 401;

    private int codeRetour;
    private String chargeUtile;
    private Vector vec;

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
}
