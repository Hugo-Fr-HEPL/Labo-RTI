package package_reseaux.other;

import Cryptage.*;
import application_billets.Application_Billets;
import database.utilities.MySQL;
import package_reseaux.Interface.*;

import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import org.bouncycastle.util.Bytes;

public class RequeteSUM implements Requete, Serializable {
    public static int CONNEXION = 1;
    public static int HANDSHAKE = 2;
    public static int ACHATDC = 3;
    public static int ACHATNC = 4;
    
    public static int SOUTE = 5;
    public static int REMARQUE = 6;
    public static int CHECKINCLOSE = 7;
    
    public Runnable createRunnable (final Socket s, final ConsoleServeur cs) {
        if (type == CONNEXION)
            return new Runnable() {
                public void run() {
                    traiteConnexion(s, cs);
                }
            };
        else if (type == HANDSHAKE) {
            return new Runnable() {
                public void run() {
                    traiteHandShake(s, cs);
                }
            };
        }
        else if (type == ACHATDC) {
            return new Runnable() {
                public void run() {
                    traiteAchatDC(s, cs);
                }
            };
        }
        else if (type == ACHATNC) {
            return new Runnable() {
                public void run() {
                    traiteAchatNC(s, cs);
                }
            };
        }
        else if (type == SOUTE) {
            return new Runnable() {
                public void run() {
                    //traiteSoute(s, cs);
                }
            };
        }
        else if (type == REMARQUE) {
            return new Runnable() {
                public void run() {
                    //traiteRemarque(s, cs);
                }
            };
        }
        else if (type == CHECKINCLOSE) {
            return new Runnable() {
                public void run() {
                    //traiteRemarque(s, cs);
                }
            };
        }
        else
            return null;
    }

    private void traiteConnexion(Socket sock, ConsoleServeur cs) {
        System.out.println("traiteConnexion");
        
        String reussite=null;
        Hashtable<String, String> HTConnection = new Hashtable<String, String>();
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
            ResultSet rs = MySQL.MySQL_Request("select nom, motDePasse from agents where fonction = \"tour-operateur\"");
            
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
                    Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchProviderException ex) {
                    Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(MessageDigest.isEqual(msgD, mdp))
                {
                    reussite="connection ok";
                    
                    CryptageAS CAS = new CryptageAS();
                    CAS.GenerateASKey();
                   
                }
                else
                    reussite="connection échouée";
            }
            else
                reussite="connection échouée";
            
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.CONNECTION, reussite);
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
    
    private void traiteHandShake(Socket sock, ConsoleServeur cs)
    {
        Vector vols7 = new Vector();
        
        CryptageAS CAS = new CryptageAS();
        
        //déchiffrement 1
        byte[] clair1 = CAS.DecrypAS(GetCleSec1());
        System.out.println("--------" + clair1.toString());
        
        //déchiffrement 2
        byte[] clair2 = CAS.DecrypAS(GetCleSec2());
        System.out.println("--------" + clair2.toString());
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT numvol, datevol, heuredepart, destination, pays, nomcompagnie, prix FROM vols inner join avions using(idavion) inner join compagnies using (idCompagnie) WHERE datevol BETWEEN DATE(NOW()) AND NOW() + INTERVAL 7 DAY");

        try {
            while(rs.next())
            {
                Vector tmp = new Vector();
                
                tmp.add(rs.getString(1));
                tmp.add(rs.getString(2));
                tmp.add(rs.getString(3));
                tmp.add(rs.getString(4));
                tmp.add(rs.getString(5));
                tmp.add(rs.getString(6));
                tmp.add(rs.getString(7));
                
                vols7.add(tmp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.HANDSHAKE, "ok", vols7);
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
    
    private void traiteAchatDC(Socket sock, ConsoleServeur cs) {  
        String reponse = null;
        byte[] infosRet = null;
        
        CryptageSY CSY = new CryptageSY();
        byte[] clair = CSY.DecryptSI(GetChargeUt(), 1);
        String informations = new String(clair);
        System.out.println("--->" + informations);
        
        String[] infos = informations.split("/");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        ResultSet rs = MySQL.MySQL_Request("SELECT nomClient, prénomClient, idClient FROM CLIENTS");

        Hashtable<String, String> HTClients = new Hashtable<String, String>();
        Hashtable<String, String> HTClients1 = new Hashtable<String, String>();
        try {
            while(rs.next())
            {
                HTClients.put(rs.getString(1), rs.getString(2));
                HTClients1.put(rs.getString(1), rs.getString(3));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String prenom = HTClients.get(infos[0]);
        if(prenom!=null)
        {
            if(prenom.equals(infos[1]))
            {
                String idCli = HTClients1.get(infos[0]);

                MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
                
                //place déjà occupées
                rs = MySQL.MySQL_Request("SELECT nombreAccompagnant from billets where numvol = " + infos[4]);
                
                int placeprise=0;
                try {
                    while(rs.next())
                    {
                        placeprise+=rs.getInt(1);
                        placeprise+=1;
                    }                    
                    
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("places occupées : " + placeprise);
                
                //place total dans l'avions
                rs = MySQL.MySQL_Request("select nombrePassager from avions inner join vols using(idavion) where numvol = " + infos[4]);
                
                int siege=0;
                try {
                    rs.next();
                    siege = rs.getInt(1);
                } catch (SQLException ex) {
                    Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("places total : " + siege);
                
                //vérif place restante
                int placeLibre = siege - placeprise;
                
                int verif = placeLibre - (Integer.parseInt(infos[2])+1);
                if(verif >=0)
                {
                    //récup dernier siège occupé
                    rs = MySQL.MySQL_Request("select coalesce(max(DernièrePlace),0) from billets where numvol = " + infos[4]);
                
                    int numSiegeOccup=0;
                    try {
                        rs.next();
                        numSiegeOccup = rs.getInt(1);
                    } catch (SQLException ex) {
                        Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("dernière siège occupé : " + numSiegeOccup);
                    int premierSiege = numSiegeOccup+1;
                    
                    MySQL.MySQL_Request("INSERT INTO BILLETS VALUES (NULL, " + infos[4] + ", " + idCli + ", " + infos[2] + ", 0, " + infos[3] + ", " + premierSiege + ", " + (premierSiege + Integer.parseInt(infos[2])) + ")");
                    
                    //récup idbillet acheté
                    rs = MySQL.MySQL_Request("select max(idbillet) from billets where numvol = " + infos[4] + " and idclient = " + idCli);
                
                    int idbillet=0;
                    try {
                        rs.next();
                        idbillet = rs.getInt(1);
                    } catch (SQLException ex) {
                        Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("id billet acheté : " + idbillet);
                    
                    //récup place achetés
                    rs = MySQL.MySQL_Request("select premièreplace, dernièreplace from billets where idbillet = " + idbillet);
                    
                    int premPlaceAch=0, derPlaceAch=0;
                    try {
                        rs.next();
                        premPlaceAch = rs.getInt(1);
                        derPlaceAch = rs.getInt(2);
                        System.out.println("interval places achetés : " + premPlaceAch + "  ->  " + derPlaceAch);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //récup prix
                    rs = MySQL.MySQL_Request("select allerRetour, prix from billets inner join vols using(numvol) where idbillet = " + idbillet);
                    
                    boolean ar=false;
                    int prix=0;
                    try {
                        rs.next();
                        ar = rs.getBoolean(1);
                        prix = rs.getInt(2);
                        System.out.println("aller-retour : " + ar + "  prix : " + prix);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    int nombrePersonne = (Integer.parseInt(infos[2])) + 1;
                    int prixTotal = prix * nombrePersonne;
                    if(ar==true)
                        prixTotal*=2;
                    
                    String infosRetClair = premPlaceAch + "/" + derPlaceAch + "/" + prixTotal;
                    System.out.println("infos retour : " + infosRetClair);
                    
                    CSY = new CryptageSY();
                    infosRet = CSY.CryptSI(infosRetClair.getBytes(), 1);
                    
                    reponse = "ok";
                }
                else
                {
                    reponse = "Plus assez de places";
                }
                
            }
            else
            {
                reponse = "Pas de client a ce nom";
            }
        }
        else
        {
            reponse = "Pas de client a ce nom";
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.ACHAT, reponse, infosRet);
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
    
    private void traiteAchatNC(Socket sock, ConsoleServeur cs) {  
        String reponse = null;
        byte[] infosRet = null;
        
        CryptageSY CSY = new CryptageSY();
        byte[] clair = CSY.DecryptSI(GetChargeUt(), 1);
        String informations = new String(clair);
        System.out.println("--->" + informations);
        
        String[] infos = informations.split("/");
        
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GetDirectory.FileDir("properties.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
        
        String sexe = null;
        if(infos[3].equals("Homme"))
            sexe="H";
        else
            sexe="F";
        
        String mdp = "mdp" + infos[0];

        MySQL.MySQL_Request("INSERT INTO clients VALUES (NULL, '" + infos[0] + "', '" + infos[1] + "', " + infos[2] + ", '" + sexe + "', '" + infos[4] + "', '" + mdp + "')");

        ResultSet rs = MySQL.MySQL_Request("SELECT max(idclient) from clients where nomclient = '" + infos[0] + "' and prénomclient = '" + infos[1] + "'");
        
        String idCli = null;
        try {
            rs.next();
            idCli = rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));

        //place déjà occupées
        rs = MySQL.MySQL_Request("SELECT nombreAccompagnant from billets where numvol = " + infos[7]);

        int placeprise=0;
        try {
            while(rs.next())
            {
                placeprise+=rs.getInt(1);
                placeprise+=1;
            }                    

        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("places occupées : " + placeprise);

        //place total dans l'avions
        rs = MySQL.MySQL_Request("select nombrePassager from avions inner join vols using(idavion) where numvol = " + infos[7]);

        int siege=0;
        try {
            rs.next();
            siege = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("places total : " + siege);

        //vérif place restante
        int placeLibre = siege - placeprise;

        int verif = placeLibre - (Integer.parseInt(infos[5])+1);
        if(verif >=0)
        {
            //récup dernier siège occupé
            rs = MySQL.MySQL_Request("select coalesce(max(DernièrePlace),0) from billets where numvol = " + infos[7]);

            int numSiegeOccup=0;
            try {
                rs.next();
                numSiegeOccup = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("dernière siège occupé : " + numSiegeOccup);
            int premierSiege = numSiegeOccup+1;

            MySQL.MySQL_Request("INSERT INTO BILLETS VALUES (NULL, " + infos[7] + ", " + idCli + ", " + infos[5] + ", 0, " + infos[6] + ", " + premierSiege + ", " + (premierSiege + Integer.parseInt(infos[5])) + ")");

            //récup idbillet acheté
            rs = MySQL.MySQL_Request("select max(idbillet) from billets where numvol = " + infos[7] + " and idclient = " + idCli);

            int idbillet=0;
            try {
                rs.next();
                idbillet = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("id billet acheté : " + idbillet);

            //récup place achetés
            rs = MySQL.MySQL_Request("select premièreplace, dernièreplace from billets where idbillet = " + idbillet);

            int premPlaceAch=0, derPlaceAch=0;
            try {
                rs.next();
                premPlaceAch = rs.getInt(1);
                derPlaceAch = rs.getInt(2);
                System.out.println("interval places achetés : " + premPlaceAch + "  ->  " + derPlaceAch);

            } catch (SQLException ex) {
                Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
            }

            //récup prix & aller-retour
            rs = MySQL.MySQL_Request("select allerRetour, prix from billets inner join vols using(numvol) where idbillet = " + idbillet);

            boolean ar=false;
            int prix=0;
            try {
                rs.next();
                ar = rs.getBoolean(1);
                prix = rs.getInt(2);
                System.out.println("aller-retour : " + ar + "  prix : " + prix);

            } catch (SQLException ex) {
                Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
            }

            int nombrePersonne = (Integer.parseInt(infos[5])) + 1;
            int prixTotal = prix * nombrePersonne;
            if(ar==true)
                prixTotal*=2;

            String infosRetClair = premPlaceAch + "/" + derPlaceAch + "/" + prixTotal;
            System.out.println("infos retour : " + infosRetClair);

            CSY = new CryptageSY();
            infosRet = CSY.CryptSI(infosRetClair.getBytes(), 1);

            reponse = "ok";
        }
        else
        {
            reponse = "Plus assez de places";
            MySQL.MySQL_Request("DELETE from clients where idclient = " + idCli);
        }
        
        ReponseSUM rep = new ReponseSUM(ReponseSUM.ACHAT, reponse, infosRet);
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
    private Socket socketClient;
    private String user;
    private byte[] password;
    private double rand;
    private int size;
    
    private byte[] cleSec1;
    private byte[] cleSec2;
    
    private byte[] ChargeUt;
    
    public RequeteSUM(int t, String cu) {
        type = t; setChargeUtile(cu);
    }
    
    public RequeteSUM(int t, byte[] cu) {
        type = t; setChargeUt(cu);
    }
    
    public RequeteSUM(int t, String u, double r, int s, byte[] p) {
        type = t; setUser(u); rand=r; size = s; setPassword(p);
    }
    
    public RequeteSUM(int t, byte[] cl, byte[] c2) {
        type = t; setCleSec1(cl); setCleSec2(c2);
    }
    
    public void setChargeUtile(String c) { chargeUtile = c; }
    public String GetChargeUtile() { return chargeUtile; }
    
    public void setUser(String u) { user = u; }
    public String GetUser() { return user; }
    
    public void setPassword(byte[] p) { password = p; }
    public byte[] GetPassword() { return password; }
    
    public void setCleSec1(byte[] c1) { cleSec1 = c1; }
    public byte[] GetCleSec1() { return cleSec1; }
    
    public void setCleSec2(byte[] c2) { cleSec2 = c2; }
    public byte[] GetCleSec2() { return cleSec2; }
    
    public void setChargeUt(byte[] cu) { ChargeUt = cu; }
    public byte[] GetChargeUt() { return ChargeUt; }
}
