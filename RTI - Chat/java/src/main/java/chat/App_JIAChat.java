package chat;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.*; 


public class App_JIAChat 
{
    static private String nomCli;
    static private InetAddress adresseGroupe;
    static MulticastSocket socketGroupe;
    static ThreadReception thr;

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Connect();
    }

    /*
    private void initComponents() {
        BDemarrer = new java.awt.Button();
        ZTNom = new java.awt.TextField();
        BArreter = new java.awt.Button();
        ZTMessage = new java.awt.TextField();
        BEnvoyer = new java.awt.Button();
        LMsgRecus = new java.awt.List();
        ...
        setLayout(new java.awt.GridLayout(2, 1));
        setTitle("Client multicast");
        ... // Instructions pour le GUI
    }
    */


    public static void Connect() {
        try {
            adresseGroupe = InetAddress.getByName("234.5.5.9");
            socketGroupe = new MulticastSocket(5001);
            socketGroupe.joinGroup(adresseGroupe);

            thr = new ThreadReception (nomCli, socketGroupe, LMsgRecus);
            thr.start();

            //nomCli = ZTNom.getText();
            nomCli = "Hugo";
            String msgDeb = nomCli + " rejoint le groupe";
            DatagramPacket dtg = new DatagramPacket(msgDeb.getBytes(), msgDeb.length(), adresseGroupe, 5001);
            socketGroupe.send(dtg);

            TimeUnit.SECONDS.sleep(1);
            System.out.println("oui " + LMsgRecus);

            BEnvoyerActionPerformed();
            TimeUnit.SECONDS.sleep(1);
            System.out.println("oui 2 " + LMsgRecus);

            BArreterActionPerformed();
        }
        catch (UnknownHostException e){ System.out.println("Erreur :-( : " + e.getMessage()); }
        catch (IOException e){ System.out.println("Erreur :-( : " + e.getMessage()); }
        catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }


    //private void BEnvoyerActionPerformed(java.awt.event.ActionEvent evt) {
    private static void BEnvoyerActionPerformed() {
        //String msg = nomCli + "> " + ZTMessage.getText();
        String msg = nomCli + "> " + "Bonjour";
        DatagramPacket dtg = new DatagramPacket(msg.getBytes(), msg.length(), adresseGroupe, 5001);
        try {
            socketGroupe.send(dtg);
        } catch (IOException e) {
            System.out.println("Erreur :-( : " + e.getMessage());
        }
    }


    //private void BArreterActionPerformed(java.awt.event.ActionEvent evt) {
    private static void BArreterActionPerformed() {
        String msg = nomCli + " quitte le groupe";
        DatagramPacket dtg = new DatagramPacket(msg.getBytes(), msg.length(), adresseGroupe, 5001);
        try {
            socketGroupe.send(dtg);
            thr.stop();

            //BDemarrer.setEnabled(true);
            //BArreter.setEnabled(false);
            //BEnvoyer.setEnabled(false);

            socketGroupe.leaveGroup(adresseGroupe); System.out.println("Après leaveGroup");

            socketGroupe.close(); System.out.println("Après close");
        } catch (IOException e) {
            System.out.println("Erreur :-( : " + e.getMessage());
        }
    }

    /*
    public static void main(String args[]) {
        new ClientChat().show();
    }
    
    private java.awt.Button BDemarrer;
    private java.awt.TextField ZTNom;
    private java.awt.Button BArreter;
    private java.awt.TextField ZTMessage;
    private java.awt.Button BEnvoyer;
    private java.awt.List LMsgRecus;
    */
    static List<String> LMsgRecus = new ArrayList<String>();
}
