package package_reseaux.other;

import database.utilities.MySQL;
import package_reseaux.Interface.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequeteSUM implements Requete, Serializable {
    public static int VOL = 1;
    public static int CHECKIN = 2;
    public static int READY = 3;
    public static int ASKTRACK = 4;
    public static int SAVETRACK = 5;
    public static int TAKINGOFF = 6;
    
    public Runnable createRunnable (final Socket s, final ConsoleServeur cs) {
        if (type == VOL)
            return new Runnable() {
                public void run() {
                    traiteVol(s, cs);
                }
            };
        else if (type == CHECKIN) {
            return new Runnable() {
                public void run() {
                    traiteCheckin(s, cs);
                }
            };
        }
        else if (type == READY) {
            return new Runnable() {
                public void run() {
                    traiteReady(s, cs);
                }
            };
        }
        else if (type == ASKTRACK) {
            return new Runnable() {
                public void run() {
                    traiteAskTrack(s, cs);
                }
            };
        }
        else if (type == SAVETRACK) {
            return new Runnable() {
                public void run() {
                    traiteSaveTrack(s, cs);
                }
            };
        }
        else if (type == TAKINGOFF) {
            return new Runnable() {
                public void run() {
                    traiteTakingOff(s, cs);
                }
            };
        }
        else
            return null;
    }

    private void traiteVol(Socket sock, ConsoleServeur cs) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT numVol, destination, pays, heureDepart, idAvion FROM vols WHERE datevol = DATE(NOW()) AND HeureDepart BETWEEN HOUR(NOW()) AND HOUR(DATE_ADD(NOW(), INTERVAL 3 HOUR))");

        Vector ret = new Vector();
        Vector infos;
        try {
            while(rs.next())
            {
                infos = new Vector();
                
                infos.add(rs.getString(1));
                infos.add(rs.getString(2));
                infos.add(rs.getString(3));
                infos.add(rs.getString(4));
                infos.add(rs.getString(5));
                
                ret.add(infos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.VOL, "", ret);
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
    
    private void traiteCheckin(Socket sock, ConsoleServeur cs){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT HeureDepart from vols WHERE numvol = " + GetChargeUtile() + " AND TIME(NOW()) >= DATE_ADD(TIME(HEUREDEPART*10000), INTERVAL -59 MINUTE) AND TIME(NOW()) <= TIME(HeureDepart*10000);");

        String result=null;
        try {
            if(rs.next())
                result = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(result!=null)
        {
            System.out.println("ok 20 min");

            //ENVOYER REQUETE A SERVEUR CHECKIN
            DataOutputStream dos = null;
            Socket cliSock = null;
            
            int port = Integer.parseInt(prop.getProperty("PORT_C"));
            String adresse = (String) prop.get("Host1");

            try {
                cliSock = new Socket(adresse, port);
                System.out.println(cliSock.getInetAddress().toString());
            }
            catch (UnknownHostException e) {
                System.err.println("Erreur ! Host non trouvé [" + e + "]");
            }
            catch (IOException e) {
                System.err.println("Erreur ! Pas de connexion ? [" + e + "]");
            }

            // Envoie de la requête
            try {
                String msgFonction = "13";
                dos = new DataOutputStream(cliSock.getOutputStream());
                dos.write(msgFonction.getBytes()); dos.flush();
                
                msgFonction = GetChargeUtile();
                DataOutputStream dos2 = new DataOutputStream(cliSock.getOutputStream());
                dos2.write(msgFonction.getBytes()); dos2.flush();
            }
            catch (IOException e) {
                System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
            }
            
            
            //ENVOYER REQUETE A SERVEUR BAGAGES
            port = Integer.parseInt(prop.getProperty("PORT_CHECKIN"));
            adresse = (String) prop.getProperty("Host1");
            
            try {
                cliSock = new Socket(adresse, port);
                System.out.println(cliSock.getInetAddress().toString());
            }
            catch (UnknownHostException e) {
                System.err.println("Erreur ! Host non trouvé [" + e + "]");
            }
            catch (IOException e) {
                System.err.println("Erreur ! Pas de connexion ? [" + e + "]");
            }

            // Envoie de la requête
            try {
                dos = new DataOutputStream(cliSock.getOutputStream());
                dos.writeUTF("7:"+GetChargeUtile()); dos.flush();
            }
            catch (IOException e) {
                System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
            }
            
            
            MySQL.MySQL_Request("UPDATE avions SET etat = 'Checkin off' WHERE idAvion = (SELECT idAvion FROM vols where numVol = " + GetChargeUtile() + ")");
            
            ReponseSUM rep = new ReponseSUM(ReponseSUM.CHECKIN, "ok");
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
        else
        {
            System.out.println("pas ok 20 min");
            
            ReponseSUM rep = new ReponseSUM(ReponseSUM.CHECKIN, "Il reste du temp");
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
    }
    
    private void traiteReady(Socket sock, ConsoleServeur cs)
    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
            
            
        //ENVOYER REQUETE A SERVEUR BAGAGES
        Socket cliSock = null;

        int port = Integer.parseInt(prop.getProperty("PORT_CHECKIN"));
        String adresse = (String) prop.get("Host1");

        try {
            cliSock = new Socket(adresse, port);
            System.out.println(cliSock.getInetAddress().toString());
        }
        catch (UnknownHostException e) {
            System.err.println("Erreur ! Host non trouvé [" + e + "]");
        }
        catch (IOException e) {
            System.err.println("Erreur ! Pas de connexion ? [" + e + "]");
        }

        // Envoie de la requête
        String msg = null;
        try {
            DataOutputStream dos = new DataOutputStream(cliSock.getOutputStream());
            dos.writeUTF("8:"+GetChargeUtile2()); dos.flush();
                
            // Read Answer
            DataInputStream dis = new DataInputStream(cliSock.getInputStream());
            msg = dis.readUTF();
            System.out.println("ooiuou " + msg);
        }
        catch (IOException e) {
            System.err.println("Erreur réseau ? [" + e.getMessage() + "]");
        }
        
        
        String msg2 = null;
        if(msg.equals("ok")) {
            // Update Frame si tous les bagages sont O ou R
            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
            MySQL.MySQL_Request("UPDATE avions SET etat = 'Ready' WHERE idAvion = " + GetChargeUtile());

            msg2 = "ok";
        } else
            msg2 = "no";
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.READY, msg2);
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
    
    private void traiteAskTrack(Socket sock, ConsoleServeur cs)
    {
        String repUtil = "Les pistes ";
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT idPiste FROM pistes where idAvion IS NULL");
        
        try {
            int i=0;
            while(rs.next())
            {
                repUtil += rs.getString(1) + ", ";
                i++;
            }
            
            if(i!=0)
            {
                repUtil = repUtil.substring(0,repUtil.length()-2);
                repUtil += " sont disponibles";
            }
            else
            {
                repUtil="Aucune piste disponible";
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.ASKTRACK, repUtil);
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
    
    private void traiteSaveTrack(Socket sock, ConsoleServeur cs)
    {
        String repUtil = null;
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT idAvion FROM pistes WHERE idPiste = " + GetChargeUtile());
        
        String occup = null;
        try {
            rs.next();
            occup = rs.getString(1);
            System.out.println("Occup = " + occup);
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(occup==null)
        {
            MySQL.MySQL_Request("UPDATE pistes SET idAvion = " + GetChargeUtile2() + " WHERE idPiste = " + GetChargeUtile());
            repUtil = "Piste réservée avec succès !";
        }
        else
        {
            repUtil = "Piste déja occupée !";
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.ASKTRACK, repUtil);
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
    
    private void traiteTakingOff(Socket sock, ConsoleServeur cs)
    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        MySQL.MySQL_Request("UPDATE avions SET etat = 'Taking off' WHERE idAvion = " + GetChargeUtile());
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.ASKTRACK, "ok");
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
    
    private int type;
    private String chargeUtile;
    private String chargeUtile2;
    private Socket socketClient;
    
    public RequeteSUM(int t, String cu) {
        type = t; setChargeUtile(cu);
    }
    
    public RequeteSUM(int t, String cu, String cu2) {
        type = t; setChargeUtile(cu); setChargeUtile2(cu2);
    }
    
    public void setChargeUtile(String c) { chargeUtile = c; }
    public String GetChargeUtile() { return chargeUtile; }
    
    public void setChargeUtile2(String c2) { chargeUtile2 = c2; }
    public String GetChargeUtile2() { return chargeUtile2; }
    
}
