package server;

import java.io.*;
import java.net.*;
import java.security.*;
import java.sql.*;
import java.util.*;

/*
Donne une adresse de classe D précise et un port PORT_CHAT précis obtenu via TCP
Vérifie l'état de voyageur ou l'appartenance à une compagnie aérienne dans la base de données
    (BD_AIRPORT contenant les informations nécessaires à l'identification)
Attend sur le port PORT_FLY une seule commande permettant de valider le voyageur ou l’employé 
*/

public class Serv_IAChat
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    private void traiteConnexion(Socket sock) {
        System.out.println("traiteConnexion");
        String reussite=null;
        
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
                    System.err.println("No Such Algorithm ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchProviderException ex) {
                    System.err.println("No Such Provider ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    System.err.println("IO Exception ? [" + ex.getMessage() + "]");
                    //Logger.getLogger(Application_Billets.class.getName()).log(Level.SEVERE, null, ex);
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
            System.err.println("SQL Exception ? [" + ex.getMessage() + "]");
            //Logger.getLogger(RequeteSUM.class.getName()).log(Level.SEVERE, null, ex);
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
}
