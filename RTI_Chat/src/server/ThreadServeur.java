package server;

import java.io.*;
import java.net.*;
import java.security.*;
import java.sql.*;
import java.util.*;


public class ThreadServeur extends Thread {
    public static String LOGIN_GROUP = "1";
    public static String LOGIN_JAVA = "JAVA";
    public static String LOGIN_C = "C";
    
    public static String ERROR_LOG = "LOGIN";
    public static String ERROR_PWD = "PASSWORD";
    public static String ERROR = "ERROR";

    Properties prop;
    int port_tcp;
    String host_udp;
    int port_udp;

    ServerSocket SSocket = null;
    Socket sock = null;
    
    int digestLength = 0;

    public ThreadServeur(Properties p) {
        prop = p;
        
        port_tcp = Integer.parseInt(prop.getProperty("Port_tcp"));
        host_udp = prop.getProperty("Host_udp");
        port_udp = Integer.parseInt(prop.getProperty("Port_udp"));
    }

    public void run() {
        try {
            SSocket = new ServerSocket(port_tcp);
        }
        catch (IOException e) { e.printStackTrace(); }

        while (!isInterrupted()) {
            try {
                System.out.println("Serveur en attente");
                sock = SSocket.accept();

                System.out.println("Le serveur est connecté");
                String[] tmp = GetMsg();

                if(tmp[0].equals(LOGIN_GROUP)) {
                    if(tmp[1].equals(LOGIN_JAVA)) {
                        if(tmp[3] == null) { // client
                            try {
                                MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
                                ResultSet rs = MySQL.MySQL_Request("SELECT nomClient FROM clients JOIN billets USING (idClient) WHERE idBillet = "+ tmp[2]);
                                
                                if(rs.next()) { // If there is at least 1 result
                                    SendMsg(host_udp +"#"+ port_udp +"#"+ rs.getString(1) +"$");
                                } else {
                                    SendMsg(ERROR_LOG +"$");
                                }
                            }
                            catch (SQLException ex) { System.err.println("SQL Exception ? [" + ex.getMessage() + "]"); }
                        } else { // agent
                            digestLength = Integer.parseInt(tmp[4]);
                            SendMsg("OK$");
                            byte[] pwdSent = GetMsgByte();
                            try {
                                MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
                                ResultSet rs = MySQL.MySQL_Request("SELECT nom, motDePasse FROM agents WHERE nom = \""+ tmp[2] +"\"");
                                
                                if(rs.next()) {
                                    String pwd = rs.getString(2);

                                    byte[] digestPwd = {};
                                    try {
                                        MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");

                                        md.update(pwd.getBytes());
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        DataOutputStream bdos = new DataOutputStream(baos);
                                        bdos.writeDouble(Double.parseDouble(tmp[3]));
                                        md.update(baos.toByteArray());
                                        digestPwd = md.digest();
                                    }
                                    catch (NoSuchAlgorithmException e) { System.out.println("--- erreur No Such Algorithm = " + e.getMessage()); }
                                    catch (NoSuchProviderException e) { System.out.println("--- erreur No Such Provider = " + e.getMessage()); }
                                    catch (IOException e) { System.out.println("--- erreur IO = " + e.getMessage()); }
                                    if(MessageDigest.isEqual(digestPwd, pwdSent))
                                        SendMsg(host_udp +"#"+ port_udp +"#"+ tmp[2] +"$");
                                    else
                                        SendMsg(ERROR_PWD +"$");
                                } else {
                                    SendMsg(ERROR_LOG +"$");
                                }
                            }
                            catch (SQLException ex) { System.err.println("SQL Exception ? [" + ex.getMessage() + "]"); }
                        }
                    } else if(tmp[1].equals(LOGIN_C)) {
                        try {
                            MySQL.MySQL_Connexion("bd_airport", prop.getProperty("DB_port"), "localhost", prop.getProperty("DB"), prop.getProperty("DB_pwd"));
                            ResultSet rs = MySQL.MySQL_Request("SELECT nom, motDePasse FROM agents WHERE nom = \""+ tmp[2] +"\" AND motDePasse = \""+ tmp[3] +"\"");

                            if(rs.next()) {
                                SendMsg(host_udp +"#"+ port_udp +"#"+ tmp[2] +"$");
                            } else {
                                SendMsg(ERROR +"$");
                            }
                        }
                        catch (SQLException ex) { System.err.println("SQL Exception ? [" + ex.getMessage() + "]"); }
                    }
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }


    public void SendMsg(String msg) {
        try {
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.write(msg.getBytes()); dos.flush();
            System.out.println("Envoi " + msg);
        }
        catch (IOException e) { System.err.println("Error network ? [" + e.getMessage() + "]"); }
    }

    public String[] GetMsg() {
        byte b;
        String[] msg = new String[10];
        String tmp = "";
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());

            int i = 0;
            msg[i] = "";
            while((b = dis.readByte()) != (byte)'$') {
                tmp += (char)b;
                if(b == '#') {
                    i++;
                    msg[i] = "";
                } else
                    msg[i] += (char)b;
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        System.out.println("Recu " + tmp);
        return msg;
    } 
    public byte[] GetMsgByte() {
        byte[] msg = {};
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            msg = dis.readNBytes(digestLength);
        }
        catch (IOException e) { e.printStackTrace(); }
        /*
        System.out.println("Get ");
        for(int i = 0; i < msg.length; i++)
            System.out.print(msg[i]);
        System.out.println(" ");
        */
        return msg;
    }
}
