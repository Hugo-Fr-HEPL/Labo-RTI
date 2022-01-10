package package_reseaux.other;

import application_bagages.Application_Bagages;
import database.utilities.MySQL;
import package_reseaux.Interface.*;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
//import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.Bytes;

public class RequeteSUM implements Requete, Serializable {
    public static int CONNEXION = 1;
    public static int BAGAGES = 2;
    
    public static int RECEPTION = 3;
    public static int DOUANE = 4;
    public static int SOUTE = 5;
    public static int REMARQUE = 6;
    
    public static int CONNEXION1 = 11;

    public static Hashtable<String, String> HTConnection = new Hashtable<String, String>();

    public Runnable createRunnable (final Socket s, final ConsoleServeur cs) {
        if (type == CONNEXION)
            return new Runnable() {
                public void run() {
                    traiteConnexion(s, cs);
                }
            };
        else if (type == BAGAGES) {
            return new Runnable() {
                public void run() {
                    traiteBagage(s, cs);
                }
            };
        }
        else if (type == RECEPTION) {
            return new Runnable() {
                public void run() {
                    traiteReception(s, cs);
                }
            };
        }
        else if (type == DOUANE) {
            return new Runnable() {
                public void run() {
                    traiteDouane(s, cs);
                }
            };
        }
        else if (type == SOUTE) {
            return new Runnable() {
                public void run() {
                    traiteSoute(s, cs);
                }
            };
        }
        else if (type == REMARQUE) {
            return new Runnable() {
                public void run() {
                    traiteRemarque(s, cs);
                }
            };
        }
        else
            return null;
    }

    private void traiteConnexion(Socket sock, ConsoleServeur cs) {
        System.out.println("traiteConnexion");
        
        String reussite=null;
        Vector volsJour = new Vector();
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
            ResultSet rs = MySQL.MySQL_Request("select nom, motDePasse from agents where fonction = \"bagagistes\"");
            
            while(rs.next())
            {
                HTConnection.put(rs.getString(1), rs.getString(2));
            }
            
            
            String nom = GetUser();
            byte[] mdp = GetPassword();
            int taille = size;
            double alea = rand;

            String m = HTConnection.get(nom);
            
            double rand=0;
            byte[] msgD={};
            if(m!=null)
            {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");

                    md.update(m.getBytes());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream bdos = new DataOutputStream(baos);
                    bdos.writeDouble(alea);
                    md.update(baos.toByteArray());
                    msgD = md.digest();
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchProviderException ex) {
                    Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Application_Bagages.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(MessageDigest.isEqual(msgD, mdp))
                {
                    reussite="connection ok";
                    
                    rs = MySQL.MySQL_Request("select numvol, nomCompagnie, destination, heureDepart from vols inner join avions using(idAvion) inner join compagnies using(idCompagnie)"/*where datevol = DATE(NOW())*/);
            
                    while(rs.next())
                    {
                        volsJour.add("VOL " + rs.getString(1) + "-" + rs.getString(2) + " - " + rs.getString(3) + " " + rs.getString(4) + "h");
                    }
                    
                }
                else
                    reussite="connection échouée";
            }
            else
                reussite="connection échouée";
            
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.CONNECTION, reussite, volsJour);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(rep); oos.flush();
            oos.close();
        }
        catch (IOException e) {
            System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
        }
    }
    
    private void traiteBagage(Socket sock, ConsoleServeur cs)
    {
        System.out.println("traiteBagage");
        Vector bagagesVols = new Vector();
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
            ResultSet rs = MySQL.MySQL_Request("select idBagage, poids, valise, receptionne, soute, verifie, remarques from bagages inner join billets using(idBillet) where numvol = " + GetChargeUtile());
            
            while(rs.next())
            {
                Vector vtmp = new Vector();
                vtmp.add(rs.getString(1));
                vtmp.add(rs.getString(2));
                vtmp.add(rs.getString(3));
                vtmp.add(rs.getBoolean(4));
                vtmp.add(rs.getString(5));
                vtmp.add(rs.getBoolean(6));
                vtmp.add(rs.getString(7));
                bagagesVols.add(vtmp);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.BAGAGE,"réponse",bagagesVols);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(rep); oos.flush();
            oos.close();
        }
        catch (IOException e) {
            System.out.println("Erreur réseau ? [" + e.getMessage() + "]");
        }
    }
    
    private void traiteReception(Socket sock, ConsoleServeur cs)
    {
        System.out.println("traiteReception");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("update bagages set receptionne = " + GetvChange() + " where idbagage = " + GetChargeUtile() + ";");
        
        System.out.println("traiteReception FIN");
    }
    
    private void traiteDouane(Socket sock, ConsoleServeur cs)
    {
        System.out.println("traiteDouane");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        MySQL.MySQL_Request("update bagages set verifie = " + GetvChange() + " where idbagage = " + GetChargeUtile() + ";");
        System.out.println("traiteDouane FIN");
    }
    
    private void traiteSoute(Socket sock, ConsoleServeur cs)
    {
        System.out.println("traiteSoute");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        MySQL.MySQL_Request("update bagages set soute = \"" + GetvChange() + "\" where idbagage = " + GetChargeUtile() + ";");
        
        System.out.println("traiteSoute FIN");
    }
    
    private void traiteRemarque(Socket sock, ConsoleServeur cs)
    {
        System.out.println("traiteRemarque");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        MySQL.MySQL_Request("update bagages set remarques = \"" + GetvChange() + "\" where idbagage = " + GetChargeUtile() + ";");
        
        System.out.println("traiteRemarque FIN");
    }

    private int type;
    private String chargeUtile;
    private String vChange;
    private Socket socketClient;
    private String user;
    private byte[] password;
    private double rand;
    private int size;

    public RequeteSUM(int t, String cu) {
        type = t; setChargeUtile(cu);
    }
    
    public RequeteSUM(int t, String cu, String vc) {
        type = t; setChargeUtile(cu); setvChange(vc);
    }
    
    public RequeteSUM(int t, String u, double r, int s, byte[] p) {
        type = t; setUser(u); rand=r; size = s; setPassword(p);
    }
    
    public void setvChange(String v) { vChange = v; }
    public String GetvChange() { return vChange; }
    
    public void setChargeUtile(String c) { chargeUtile = c; }
    public String GetChargeUtile() { return chargeUtile; }
    
    public void setUser(String u) { user = u; }
    public String GetUser() { return user; }
    
    public void setPassword(byte[] p) { password = p; }
    public byte[] GetPassword() { return password; }
}
